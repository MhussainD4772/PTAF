"""Database connection handling and SQL execution (Java db package port)."""

from __future__ import annotations

import logging
from contextvars import ContextVar
from typing import Any, Literal
from urllib.parse import parse_qs, unquote

from ptaf.utils import config, yaml_reader

logger = logging.getLogger(__name__)

DriverType = Literal["postgresql", "mssql"]

_connection: ContextVar[Any | None] = ContextVar("_db_connection", default=None)
_driver_type: ContextVar[DriverType | None] = ContextVar("_db_driver_type", default=None)


class DatabaseUnavailableError(RuntimeError):
    """Raised when database configuration or connectivity is unavailable."""


def is_database_configured() -> bool:
    """Return True when required DB settings and password env var are present."""
    url = config.get_database_connection_url()
    username = config.get_database_username()
    password_env = config.get_value("database.password_env_variable")
    if not url or not username or not password_env:
        return False
    return config.get_database_password() is not None


def _infer_driver_type(connection_url: str) -> DriverType:
    lowered = connection_url.lower()
    if lowered.startswith("jdbc:postgresql:") or lowered.startswith("postgresql:"):
        return "postgresql"
    if lowered.startswith("jdbc:sqlserver:") or lowered.startswith("sqlserver:"):
        return "mssql"
    raise ValueError(
        "Unsupported database connection URL. Expected PostgreSQL or SQL Server JDBC URL."
    )


def _parse_postgresql_jdbc_url(connection_url: str) -> dict[str, str | int]:
    normalized = connection_url
    for prefix in ("jdbc:postgresql://", "postgresql://"):
        if normalized.startswith(prefix):
            normalized = normalized[len(prefix) :]
            break

    if "/" not in normalized:
        raise ValueError(f"Invalid PostgreSQL connection URL: {connection_url}")

    host_port, dbname = normalized.split("/", 1)
    dbname = unquote(dbname.split("?", 1)[0])
    if ":" in host_port:
        host, port_str = host_port.rsplit(":", 1)
        port = int(port_str)
    else:
        host = host_port
        port = 5432
    return {"host": host, "port": port, "dbname": dbname}


def _parse_sqlserver_jdbc_url(connection_url: str) -> dict[str, str]:
    normalized = connection_url
    for prefix in ("jdbc:sqlserver://", "sqlserver://"):
        if normalized.startswith(prefix):
            normalized = normalized[len(prefix) :]
            break

    host_port, _, query_string = normalized.partition(";")
    if ":" in host_port:
        server, port = host_port.rsplit(":", 1)
    else:
        server, port = host_port, "1433"

    params = parse_qs(query_string, keep_blank_values=True)
    for segment in query_string.split(";"):
        if "=" not in segment:
            continue
        key, value = segment.split("=", 1)
        params.setdefault(key.strip().lower(), [value.strip()])

    database = (
        (params.get("databasename") or params.get("database") or [""])[0]
    )
    if not database:
        raise ValueError(
            f"SQL Server JDBC URL must include databaseName: {connection_url}"
        )

    return {"server": server, "port": port, "database": database}


def _adapt_sql_placeholders(sql: str, driver_type: DriverType) -> str:
    if driver_type == "postgresql":
        return sql.replace("?", "%s")
    return sql


class DatabaseHandler:
    """Manages lifecycle of a database connection per execution context."""

    @staticmethod
    def get_connection() -> Any:
        connection = _connection.get()
        if connection is not None:
            if _is_connection_open(connection):
                return connection
            DatabaseHandler.close_connection()

        logger.info("No existing connection found for this context. Creating a new one.")
        connection, driver_type = _create_connection()
        _connection.set(connection)
        _driver_type.set(driver_type)
        return connection

    @staticmethod
    def close_connection() -> None:
        connection = _connection.get()
        try:
            if connection is not None and _is_connection_open(connection):
                connection.close()
                logger.info("Database connection for this context has been closed.")
        except Exception:
            logger.exception("Failed to close database connection.")
        finally:
            _connection.set(None)
            _driver_type.set(None)

    @staticmethod
    def get_driver_type() -> DriverType:
        driver_type = _driver_type.get()
        if driver_type is None:
            url = config.get_database_connection_url()
            if not url:
                raise DatabaseUnavailableError(
                    "Database connection URL is not configured."
                )
            return _infer_driver_type(url)
        return driver_type


def _is_connection_open(connection: Any) -> bool:
    closed = getattr(connection, "closed", None)
    if callable(closed):
        return not closed()
    if isinstance(closed, bool):
        return not closed
    return True


def _create_connection() -> tuple[Any, DriverType]:
    url = config.get_database_connection_url()
    username = config.get_database_username()
    password = config.get_database_password()
    password_env_variable = config.get_value("database.password_env_variable")

    if not url or not username:
        raise DatabaseUnavailableError(
            "Database URL or username is not set in the configuration file."
        )
    if password is None:
        raise DatabaseUnavailableError(
            "Database password environment variable "
            f"'{password_env_variable}' is not set."
        )

    driver_type = _infer_driver_type(url)
    logger.info("Attempting to connect to database at URL: %s", url)

    if driver_type == "postgresql":
        import psycopg

        pg_config = _parse_postgresql_jdbc_url(url)
        connection = psycopg.connect(
            host=pg_config["host"],
            port=pg_config["port"],
            dbname=pg_config["dbname"],
            user=username,
            password=password,
        )
        return connection, driver_type

    import pyodbc

    mssql_config = _parse_sqlserver_jdbc_url(url)
    conn_str = (
        "DRIVER={ODBC Driver 18 for SQL Server};"
        f"SERVER={mssql_config['server']},{mssql_config['port']};"
        f"DATABASE={mssql_config['database']};"
        f"UID={username};"
        f"PWD={password};"
        "TrustServerCertificate=yes;"
    )
    connection = pyodbc.connect(conn_str)
    return connection, driver_type


class DatabaseActionPerformer:
    """Low-level parameterized SQL execution."""

    def execute_query(
        self, connection: Any, sql: str, params: list[Any]
    ) -> list[dict[str, Any]]:
        driver_type = DatabaseHandler.get_driver_type()
        prepared_sql = _adapt_sql_placeholders(sql, driver_type)
        logger.debug("Executing Query: %s", prepared_sql)
        results: list[dict[str, Any]] = []

        if driver_type == "postgresql":
            with connection.cursor() as cursor:
                cursor.execute(prepared_sql, params)
                if cursor.description is None:
                    return results
                column_names = [desc.name for desc in cursor.description]
                for row in cursor.fetchall():
                    results.append(dict(zip(column_names, row, strict=False)))
        else:
            cursor = connection.cursor()
            try:
                cursor.execute(prepared_sql, params)
                columns = [column[0] for column in cursor.description]
                for row in cursor.fetchall():
                    results.append(dict(zip(columns, row, strict=False)))
            finally:
                cursor.close()

        logger.debug("Query returned %s rows.", len(results))
        return results

    def execute_update(
        self, connection: Any, sql: str, params: list[Any]
    ) -> int:
        driver_type = DatabaseHandler.get_driver_type()
        prepared_sql = _adapt_sql_placeholders(sql, driver_type)
        logger.debug("Executing Update: %s", prepared_sql)

        if driver_type == "postgresql":
            with connection.cursor() as cursor:
                cursor.execute(prepared_sql, params)
                affected_rows = cursor.rowcount
                connection.commit()
        else:
            cursor = connection.cursor()
            try:
                cursor.execute(prepared_sql, params)
                affected_rows = cursor.rowcount
                connection.commit()
            finally:
                cursor.close()

        logger.debug("%s rows were affected.", affected_rows)
        return affected_rows


class DatabaseActionImpl:
    """Orchestrates YAML query lookup and database execution."""

    def __init__(self) -> None:
        self._performer = DatabaseActionPerformer()

    def perform_query(self, query_key: str, *params: object) -> list[dict[str, Any]]:
        sql = yaml_reader.get(query_key)
        if not isinstance(sql, str):
            logger.error("Query key '%s' not found in any YAML files.", query_key)
            raise ValueError(f"Query key not found: {query_key}")

        logger.info(
            "Performing query for key '%s' with parameters: %s",
            query_key,
            list(params),
        )
        try:
            connection = DatabaseHandler.get_connection()
            return self._performer.execute_query(connection, sql, list(params))
        except Exception:
            logger.exception("Failed to execute query for key '%s'", query_key)
            return []

    def perform_update(self, query_key: str, *params: object) -> int:
        sql = yaml_reader.get(query_key)
        if not isinstance(sql, str):
            logger.error("Query key '%s' not found in any YAML files.", query_key)
            raise ValueError(f"Query key not found: {query_key}")

        logger.info(
            "Performing update for key '%s' with parameters: %s",
            query_key,
            list(params),
        )
        try:
            connection = DatabaseHandler.get_connection()
            return self._performer.execute_update(connection, sql, list(params))
        except Exception:
            logger.exception("Failed to execute update for key '%s'", query_key)
            return -1

    def record_exists(self, query_key: str, *params: object) -> bool:
        exists = bool(self.perform_query(query_key, *params))
        logger.info(
            "Verifying record existence for key '%s'. Result: %s",
            query_key,
            exists,
        )
        return exists

    def get_single_record(
        self, query_key: str, *params: object
    ) -> dict[str, Any] | None:
        results = self.perform_query(query_key, *params)
        if not results:
            logger.warning("Query for key '%s' returned no results.", query_key)
            return None
        if len(results) > 1:
            logger.error(
                "Query for key '%s' returned %s records, but only one was expected.",
                query_key,
                len(results),
            )
            raise RuntimeError(
                f"Expected single record, but found {len(results)}"
            )
        return results[0]

    def get_single_value(self, query_key: str, *params: object) -> object | None:
        record = self.get_single_record(query_key, *params)
        if not record:
            logger.warning(
                "Cannot get single value, as no record was found for key '%s'",
                query_key,
            )
            return None
        if len(record) > 1:
            logger.warning(
                "Record has multiple columns, returning the first value only "
                "for key '%s'",
                query_key,
            )
        return next(iter(record.values()))

"""Unit tests for database handler helpers."""

from __future__ import annotations

from types import SimpleNamespace
from unittest.mock import MagicMock

import pytest

from ptaf.db.db_handler import (
    DatabaseActionPerformer,
    _adapt_sql_placeholders,
    _infer_driver_type,
    _parse_postgresql_jdbc_url,
    is_database_configured,
)


class TestDatabaseUrlHelpers:
    def test_infer_postgresql_driver(self) -> None:
        assert (
            _infer_driver_type("jdbc:postgresql://localhost:5432/app")
            == "postgresql"
        )

    def test_infer_mssql_driver(self) -> None:
        assert (
            _infer_driver_type(
                "jdbc:sqlserver://localhost:1433;databaseName=app"
            )
            == "mssql"
        )

    def test_parse_postgresql_jdbc_url(self) -> None:
        parsed = _parse_postgresql_jdbc_url(
            "jdbc:postgresql://localhost:5432/lime-of-time-database"
        )
        assert parsed == {
            "host": "localhost",
            "port": 5432,
            "dbname": "lime-of-time-database",
        }

    def test_adapt_sql_placeholders_for_postgresql(self) -> None:
        sql = "SELECT * FROM users WHERE email = ? AND id = ?;"
        assert _adapt_sql_placeholders(sql, "postgresql") == (
            "SELECT * FROM users WHERE email = %s AND id = %s;"
        )

    def test_adapt_sql_placeholders_for_mssql(self) -> None:
        sql = "SELECT * FROM users WHERE email = ?;"
        assert _adapt_sql_placeholders(sql, "mssql") == sql


class TestDatabaseConfiguration:
    def test_is_database_configured_without_password(
        self, monkeypatch: pytest.MonkeyPatch
    ) -> None:
        monkeypatch.delenv("DB_PASSWORD", raising=False)
        assert is_database_configured() is False

    def test_is_database_configured_with_password(
        self, monkeypatch: pytest.MonkeyPatch
    ) -> None:
        monkeypatch.setenv("DB_PASSWORD", "secret")
        assert is_database_configured() is True


class TestDatabaseActionPerformer:
    def test_execute_query_builds_row_maps(self) -> None:
        cursor = MagicMock()
        cursor.description = [
            SimpleNamespace(name="email"),
            SimpleNamespace(name="username"),
        ]
        cursor.fetchall.return_value = [
            ("new.user@test.com", "newuser"),
        ]

        connection = MagicMock()
        connection.cursor.return_value.__enter__.return_value = cursor

        performer = DatabaseActionPerformer()
        rows = performer.execute_query(
            connection,
            "SELECT email, username FROM users WHERE email = ?;",
            ["new.user@test.com"],
        )

        cursor.execute.assert_called_once_with(
            "SELECT email, username FROM users WHERE email = %s;",
            ["new.user@test.com"],
        )
        assert rows == [
            {"email": "new.user@test.com", "username": "newuser"},
        ]

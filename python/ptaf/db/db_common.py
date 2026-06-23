"""High-level database helpers for step definitions (DatabaseCommonMethods.java port)."""

from __future__ import annotations

import logging

from ptaf.db.db_handler import DatabaseActionImpl

logger = logging.getLogger(__name__)


class DatabaseCommonMethods:
    """Translates readable method calls into database actions."""

    def __init__(self) -> None:
        self._database_action = DatabaseActionImpl()

    def get_records(
        self, query_key: str, *params: object
    ) -> list[dict[str, object]]:
        logger.info("Getting records for query key: %s", query_key)
        return self._database_action.perform_query(query_key, *params)

    def get_single_record(
        self, query_key: str, *params: object
    ) -> dict[str, object] | None:
        logger.info("Getting single record for query key: %s", query_key)
        return self._database_action.get_single_record(query_key, *params)

    def get_single_value(self, query_key: str, *params: object) -> object | None:
        logger.info("Getting single value for query key: %s", query_key)
        return self._database_action.get_single_value(query_key, *params)

    def verify_record_exists(self, query_key: str, *params: object) -> None:
        logger.info("Verifying record exists for query key: %s", query_key)
        exists = self._database_action.record_exists(query_key, *params)
        assert exists, (
            f"Verification failed: Record for query '{query_key}' was not found."
        )
        logger.info(
            "Success: Record for query key '%s' found as expected.",
            query_key,
        )

    def verify_record_does_not_exist(self, query_key: str, *params: object) -> None:
        logger.info("Verifying record does NOT exist for query key: %s", query_key)
        exists = self._database_action.record_exists(query_key, *params)
        assert not exists, (
            "Verification failed: Record for query "
            f"'{query_key}' was found but not expected."
        )
        logger.info(
            "Success: Record for query key '%s' was not found, as expected.",
            query_key,
        )

    def verify_rows_affected(
        self, expected_rows_affected: int, query_key: str, *params: object
    ) -> None:
        logger.info(
            "Executing update for query key '%s' and verifying %s rows affected.",
            query_key,
            expected_rows_affected,
        )
        actual_rows_affected = self._database_action.perform_update(
            query_key, *params
        )
        assert actual_rows_affected == expected_rows_affected, (
            "Verification failed: Unexpected number of rows affected."
        )
        logger.info(
            "Success: %s rows were affected as expected.",
            actual_rows_affected,
        )

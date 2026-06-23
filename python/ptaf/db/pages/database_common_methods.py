"""High-level database helpers for step definitions (M7 will flesh out implementation)."""

from __future__ import annotations


class DatabaseCommonMethods:
    """Translates readable method calls into database actions."""

    def verify_record_does_not_exist(
        self, query_key: str, *params: object
    ) -> None:
        raise NotImplementedError("TODO(migration): port DatabaseActionImpl.recordExists")

    def verify_record_exists(self, query_key: str, *params: object) -> None:
        raise NotImplementedError("TODO(migration): port DatabaseActionImpl.recordExists")

    def verify_rows_affected(
        self, expected_rows: int, query_key: str, *params: object
    ) -> None:
        raise NotImplementedError(
            "TODO(migration): port DatabaseActionImpl.performUpdate"
        )

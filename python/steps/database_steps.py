"""Gherkin step definitions for database testing (DatabaseSteps.java)."""

from __future__ import annotations

from pytest_bdd import given, parsers, then, when

from ptaf.db.db_common import DatabaseCommonMethods

_db_methods = DatabaseCommonMethods()


def _parse_parameters(param_string: str) -> tuple[object, ...]:
    if param_string is None or param_string.strip() == "":
        return ()
    return tuple(part.strip() for part in param_string.split(","))


@given(
    parsers.parse(
        'the database does not contain a record for query "{query_key}" '
        'with parameters "{params}"'
    )
)
def the_database_does_not_contain_a_record_for_query(
    query_key: str, params: str
) -> None:
    _db_methods.verify_record_does_not_exist(query_key, *_parse_parameters(params))


@then(
    parsers.parse(
        'I verify the database contains a record for query "{query_key}" '
        'with parameters "{params}"'
    )
)
def i_verify_the_database_contains_a_record_for_query_with_parameters(
    query_key: str, params: str
) -> None:
    _db_methods.verify_record_exists(query_key, *_parse_parameters(params))


@then(
    parsers.parse(
        'I verify the database does not contain a record for query "{query_key}" '
        'with parameters "{params}"'
    )
)
def i_verify_the_database_does_not_contain_a_record_for_query_with_parameters(
    query_key: str, params: str
) -> None:
    _db_methods.verify_record_does_not_exist(query_key, *_parse_parameters(params))


@when(
    parsers.parse(
        'I insert a new record using query "{query_key}" with parameters "{params}"'
    )
)
def i_insert_a_new_record_using_query_with_parameters(
    query_key: str, params: str
) -> None:
    _db_methods.verify_rows_affected(1, query_key, *_parse_parameters(params))


@when(
    parsers.parse(
        'I update a record using query "{query_key}" with parameters "{params}"'
    )
)
def i_update_a_record_using_query_with_parameters(
    query_key: str, params: str
) -> None:
    _db_methods.verify_rows_affected(1, query_key, *_parse_parameters(params))


@when(
    parsers.parse(
        'I delete {expected_rows:d} record(s) using query "{query_key}" '
        'with parameters "{params}"'
    )
)
def i_delete_records_using_query_with_parameters(
    expected_rows: int, query_key: str, params: str
) -> None:
    _db_methods.verify_rows_affected(
        expected_rows, query_key, *_parse_parameters(params)
    )

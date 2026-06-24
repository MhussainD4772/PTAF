"""Gherkin step definitions for API testing (ApiSteps.java)."""

from __future__ import annotations

from pytest_bdd import parsers

from steps.step_binding import step

from ptaf.api.api_common import ApiCommonMethods

_api_methods = ApiCommonMethods()


@step(parsers.parse('I set the request header "{key}" to "{value}"'))
def i_set_the_request_header(key: str, value: str) -> None:
    _api_methods.set_header(key, value)


@step(parsers.parse('I set the path parameter "{key}" to "{value}"'))
def i_set_the_path_parameter(key: str, value: str) -> None:
    _api_methods.set_path_parameter(key, value)


@step(parsers.parse('I set the query parameter "{key}" to "{value}"'))
def i_set_the_query_parameter(key: str, value: str) -> None:
    _api_methods.set_query_parameter(key, value)


@step("I set the request body to")
def i_set_the_request_body_to(docstring: str) -> None:
    _api_methods.set_request_body(docstring)


@step(
    parsers.parse(
        'I send a "{request_key}" request to the "{service_name}" service'
    )
)
def i_send_a_request_to_the_service(request_key: str, service_name: str) -> None:
    _api_methods.send_request(service_name, request_key)


@step(parsers.parse("the response code should be {expected_status_code:d}"))
def the_response_code_should_be(expected_status_code: int) -> None:
    _api_methods.verify_response_status_code(expected_status_code)


@step(
    parsers.parse('the response body should contain the text "{expected_text}"')
)
def the_response_body_should_contain_the_text(expected_text: str) -> None:
    _api_methods.verify_response_body_contains(expected_text)


@step(
    parsers.parse(
        'the response header "{header_name}" should be "{expected_value}"'
    )
)
def the_response_header_should_be(header_name: str, expected_value: str) -> None:
    _api_methods.verify_response_header(header_name, expected_value)


@step(
    parsers.parse(
        'the value of the JSON path "{json_path}" should be "{expected_value}"'
    )
)
def the_value_of_the_json_path_should_be(
    json_path: str, expected_value: str
) -> None:
    _api_methods.verify_json_path_value(json_path, expected_value)

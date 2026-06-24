"""High-level API helpers for step definitions (ApiCommonMethods.java port)."""

from __future__ import annotations

import logging

from ptaf.api.api_client import ApiActionImpl

logger = logging.getLogger(__name__)


class ApiCommonMethods:
    """Translates readable method calls into API actions."""

    def __init__(self) -> None:
        self._api_action = ApiActionImpl()

    def set_header(self, key: str, value: str) -> None:
        logger.info("Setting header: %s = %s", key, value)
        self._api_action.set_header(key, value)

    def set_path_parameter(self, key: str, value: str) -> None:
        logger.info("Setting path parameter: %s = %s", key, value)
        self._api_action.set_path_parameter(key, value)

    def set_query_parameter(self, key: str, value: object) -> None:
        logger.info("Setting query parameter: %s = %s", key, value)
        self._api_action.set_query_parameter(key, value)

    def set_request_body(self, body: object) -> None:
        logger.info("Setting request body.")
        logger.debug("Request body content: %s", body)
        self._api_action.set_request_body(body)

    def send_request(self, service_name: str, request_key: str) -> None:
        logger.info(
            "Sending request for key '%s' to service '%s'",
            request_key,
            service_name,
        )
        self._api_action.send_request(service_name, request_key)

    def verify_response_status_code(self, expected_status_code: int) -> None:
        actual_status_code = self._api_action.get_response_status_code()
        logger.info(
            "Verifying response status code. Expected: %s, Actual: %s",
            expected_status_code,
            actual_status_code,
        )
        assert actual_status_code == expected_status_code, (
            "Response status code mismatch."
        )

    def verify_response_body_contains(self, expected_text: str) -> None:
        response_body = self._api_action.get_response_body()
        logger.info("Verifying response body contains text: '%s'", expected_text)
        assert expected_text in response_body, (
            "Response body did not contain the expected text. "
            f"Expected: {expected_text}"
        )

    def verify_response_header(self, header_name: str, expected_value: str) -> None:
        headers = self._api_action.get_last_response().headers
        actual_value = next(
            (
                value
                for key, value in headers.items()
                if key.lower() == header_name.lower()
            ),
            None,
        )
        logger.info(
            "Verifying header '%s'. Expected: '%s', Actual: '%s'",
            header_name,
            expected_value,
            actual_value,
        )
        assert actual_value is not None, (
            f"Header '{header_name}' not found in response."
        )
        assert actual_value == expected_value, "Response header value mismatch."

    def verify_json_path_value(self, json_path: str, expected_value: str) -> None:
        actual_value_obj = self.get_value_by_json_path(json_path)
        actual_value = None if actual_value_obj is None else str(actual_value_obj)
        logger.info(
            "Verifying JSONPath '%s'. Expected: '%s', Actual: '%s'",
            json_path,
            expected_value,
            actual_value,
        )
        assert actual_value == expected_value, (
            "Value from JSONPath did not match expected value."
        )

    def get_value_by_json_path(self, json_path: str) -> object | None:
        logger.info("Getting value from response using JSONPath: %s", json_path)
        return self._api_action.get_value_from_response(json_path)

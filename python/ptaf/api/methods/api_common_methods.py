"""High-level API helpers for step definitions (M7 will flesh out implementation)."""

from __future__ import annotations


class ApiCommonMethods:
    """Translates readable method calls into API actions."""

    def set_header(self, key: str, value: str) -> None:
        raise NotImplementedError("TODO(migration): port ApiActionImpl.setHeader")

    def set_path_parameter(self, key: str, value: str) -> None:
        raise NotImplementedError("TODO(migration): port ApiActionImpl.setPathParameter")

    def set_query_parameter(self, key: str, value: str) -> None:
        raise NotImplementedError("TODO(migration): port ApiActionImpl.setQueryParameter")

    def set_request_body(self, body: str) -> None:
        raise NotImplementedError("TODO(migration): port ApiActionImpl.setRequestBody")

    def send_request(self, service_name: str, request_key: str) -> None:
        raise NotImplementedError("TODO(migration): port ApiActionImpl.sendRequest")

    def verify_response_status_code(self, expected_status_code: int) -> None:
        raise NotImplementedError(
            "TODO(migration): port ApiActionImpl.getResponseStatusCode"
        )

    def verify_response_body_contains(self, expected_text: str) -> None:
        raise NotImplementedError(
            "TODO(migration): port ApiActionImpl.getResponseBody"
        )

    def verify_response_header(self, header_name: str, expected_value: str) -> None:
        raise NotImplementedError("TODO(migration): port ApiActionImpl response headers")

    def verify_json_path_value(self, json_path: str, expected_value: str) -> None:
        raise NotImplementedError("TODO(migration): port JSON path verification")

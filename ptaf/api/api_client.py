"""Playwright API request handling and action implementation (Java api package port)."""

from __future__ import annotations

import json
import logging
import time
from contextvars import ContextVar
from dataclasses import dataclass
from typing import Any

from playwright.sync_api import APIRequestContext, Playwright, sync_playwright

from ptaf import hooks
from ptaf.utils import config, yaml_reader

logger = logging.getLogger(__name__)

_api_context: ContextVar[APIRequestContext | None] = ContextVar(
    "_api_context", default=None
)
_playwright: ContextVar[Playwright | None] = ContextVar("_api_playwright", default=None)
_owns_playwright: ContextVar[bool] = ContextVar("_api_owns_playwright", default=False)
_headers: ContextVar[dict[str, str] | None] = ContextVar("_api_headers", default=None)
_path_params: ContextVar[dict[str, str] | None] = ContextVar(
    "_api_path_params", default=None
)
_query_params: ContextVar[dict[str, Any] | None] = ContextVar(
    "_api_query_params", default=None
)
_request_body: ContextVar[Any | None] = ContextVar("_api_request_body", default=None)
_last_response: ContextVar[ApiResponseWrapper | None] = ContextVar(
    "_api_last_response", default=None
)


@dataclass(frozen=True)
class ApiResponseWrapper:
    """Standardized API response decoupled from Playwright's APIResponse."""

    status_code: int
    body: str
    headers: dict[str, str]


def _mutable_dict(initial: ContextVar[dict[str, Any] | None]) -> dict[str, Any]:
    value = initial.get()
    if value is None:
        value = {}
        initial.set(value)
    return value


class ApiRequestHandler:
    """Manages lifecycle of the Playwright APIRequestContext per execution context."""

    @staticmethod
    def get_context(service_name: str) -> APIRequestContext:
        context = _api_context.get()
        if context is not None:
            return context

        logger.info("Creating new APIRequestContext for service: %s", service_name)

        playwright = hooks.get_playwright()
        if playwright is None:
            playwright = sync_playwright().start()
            _owns_playwright.set(True)
        else:
            _owns_playwright.set(False)
        _playwright.set(playwright)

        base_url = config.get_api_service_base_url(service_name)
        if not base_url:
            raise ValueError(
                f"Base URL for API service '{service_name}' not found in config.yml."
            )

        extra_headers: dict[str, str] = {}
        token_env_var = config.get_value(
            f"api_services.{service_name}.auth_token_env"
        )
        if token_env_var:
            token = config.get_api_service_auth_token(service_name)
            if not token:
                raise ValueError(
                    "API auth token environment variable "
                    f"'{token_env_var}' is not set or is empty."
                )
            extra_headers["Authorization"] = f"Bearer {token}"
            logger.info("API context created with Authorization header.")

        context = playwright.request.new_context(
            base_url=base_url,
            extra_http_headers=extra_headers or None,
        )
        _api_context.set(context)
        return context

    @staticmethod
    def dispose_context() -> None:
        context = _api_context.get()
        if context is not None:
            context.dispose()
            _api_context.set(None)
            logger.info("APIRequestContext disposed for this context.")

        if _owns_playwright.get():
            playwright = _playwright.get()
            if playwright is not None:
                playwright.stop()
            _playwright.set(None)
            _owns_playwright.set(False)


class ApiActionPerformer:
    """Low-level HTTP request execution via Playwright APIRequestContext."""

    @staticmethod
    def _replace_path_parameters(
        endpoint: str, path_params: dict[str, str] | None
    ) -> str:
        if not path_params:
            return endpoint
        processed = endpoint
        for key, value in path_params.items():
            processed = processed.replace(f"{{{key}}}", value)
        return processed

    @staticmethod
    def _serialize_body(body: Any) -> str:
        if isinstance(body, str):
            text = body.strip()
            try:
                json.loads(text)
                return text
            except json.JSONDecodeError:
                return json.dumps(body)
        return json.dumps(body)

    def send_request(
        self,
        context: APIRequestContext,
        method: str,
        endpoint: str,
        headers: dict[str, str] | None,
        query_params: dict[str, Any] | None,
        path_params: dict[str, str] | None,
        body: Any | None,
    ) -> ApiResponseWrapper:
        processed_endpoint = self._replace_path_parameters(endpoint, path_params)
        logger.info(
            "Sending %s request to endpoint: %s",
            method.upper(),
            processed_endpoint,
        )

        request_headers = dict(headers or {})
        request_params = {
            key: str(value) for key, value in (query_params or {}).items()
        }
        data: str | None = None
        if body is not None:
            data = self._serialize_body(body)
            if "Content-Type" not in request_headers and "content-type" not in {
                key.lower() for key in request_headers
            }:
                request_headers["Content-Type"] = "application/json"
            logger.debug("Request Body: %s", data)

        start_time = time.monotonic()
        method_upper = method.upper()
        request_kwargs: dict[str, Any] = {}
        if request_headers:
            request_kwargs["headers"] = request_headers
        if request_params:
            request_kwargs["params"] = request_params
        if data is not None:
            request_kwargs["data"] = data

        if method_upper == "GET":
            response = context.get(processed_endpoint, **request_kwargs)
        elif method_upper == "POST":
            response = context.post(processed_endpoint, **request_kwargs)
        elif method_upper == "PUT":
            response = context.put(processed_endpoint, **request_kwargs)
        elif method_upper == "DELETE":
            response = context.delete(processed_endpoint, **request_kwargs)
        elif method_upper == "PATCH":
            response = context.patch(processed_endpoint, **request_kwargs)
        else:
            raise ValueError(f"Unsupported HTTP method: {method}")

        duration_ms = int((time.monotonic() - start_time) * 1000)
        response_body = response.text()
        logger.info(
            "Received response with Status: %s in %sms",
            response.status,
            duration_ms,
        )
        logger.debug("Response Body: %s", response_body)

        return ApiResponseWrapper(
            status_code=response.status,
            body=response_body,
            headers=dict(response.headers),
        )


class ApiActionImpl:
    """Stateful API action implementation with request building and response access."""

    def __init__(self) -> None:
        self._performer = ApiActionPerformer()

    def set_header(self, key: str, value: str) -> None:
        _mutable_dict(_headers)[key] = value

    def set_path_parameter(self, key: str, value: str) -> None:
        _mutable_dict(_path_params)[key] = value

    def set_query_parameter(self, key: str, value: object) -> None:
        _mutable_dict(_query_params)[key] = value

    def set_request_body(self, body: object) -> None:
        _request_body.set(body)

    def send_request(self, service_name: str, request_key: str) -> ApiResponseWrapper:
        method = yaml_reader.get(f"{request_key}.method")
        endpoint = yaml_reader.get(f"{request_key}.endpoint")

        if not isinstance(method, str) or not isinstance(endpoint, str):
            raise ValueError(
                "Request definition for key "
                f"'{request_key}' not found or is incomplete in api_requests.yml."
            )

        response = self._performer.send_request(
            ApiRequestHandler.get_context(service_name),
            method,
            endpoint,
            _headers.get() or {},
            _query_params.get() or {},
            _path_params.get() or {},
            _request_body.get(),
        )
        _last_response.set(response)
        self._clear_request_state()
        return response

    def get_last_response(self) -> ApiResponseWrapper:
        response = _last_response.get()
        if response is None:
            raise RuntimeError(
                "No API request has been sent yet in this scenario. "
                "Cannot get a response."
            )
        return response

    def get_response_status_code(self) -> int:
        return self.get_last_response().status_code

    def get_response_body(self) -> str:
        return self.get_last_response().body

    def get_value_from_response(self, json_path: str) -> object | None:
        body = self.get_response_body()
        if not body:
            logger.warning(
                "Cannot get value from JSONPath because response body is empty."
            )
            return None
        try:
            from jsonpath_ng.ext import parse

            matches = parse(json_path).find(json.loads(body))
            if not matches:
                return None
            return matches[0].value
        except Exception as exc:
            logger.error(
                "Failed to read JSONPath '%s' from response body.",
                json_path,
                exc_info=True,
            )
            raise RuntimeError(
                "Invalid JSONPath expression or body format."
            ) from exc

    def _clear_request_state(self) -> None:
        headers = _headers.get()
        if headers is not None:
            headers.clear()
        path_params = _path_params.get()
        if path_params is not None:
            path_params.clear()
        query_params = _query_params.get()
        if query_params is not None:
            query_params.clear()
        _request_body.set(None)
        logger.debug("Request state cleared for the current context.")

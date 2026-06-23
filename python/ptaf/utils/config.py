"""Configuration property accessors backed by merged YAML resources."""

from __future__ import annotations

import os

from ptaf.utils import yaml_reader


def get_base_url(url: str) -> str | None:
    value = yaml_reader.get(url)
    return value if isinstance(value, str) else None


def get_browser() -> str | None:
    value = yaml_reader.get("browser")
    return value if isinstance(value, str) else None


def get_headless_mode() -> str | None:
    value = yaml_reader.get("headless")
    return value if isinstance(value, str) else None


def get_yaml_store_location() -> str | None:
    value = yaml_reader.get("yamlStoreLocation")
    return value if isinstance(value, str) else None


def get_excel_document_location() -> str | None:
    value = yaml_reader.get("excelDocumentLocation")
    return value if isinstance(value, str) else None


def get_video_capture() -> str | None:
    value = yaml_reader.get("videoCapture")
    return value if isinstance(value, str) else None


def get_value(value: str) -> str | None:
    result = yaml_reader.get(value)
    return result if isinstance(result, str) else None


def get_database_connection_url() -> str | None:
    return get_value("database.connection_url")


def get_database_username() -> str | None:
    return get_value("database.username")


def get_database_password() -> str | None:
    """Read DB password from the env var named in config (not stored in YAML)."""
    password_env_variable = get_value("database.password_env_variable")
    if not password_env_variable:
        return None
    return os.getenv(password_env_variable)


def get_api_service_base_url(service_name: str) -> str | None:
    return get_value(f"api_services.{service_name}.base_url")


def get_api_service_auth_token(service_name: str) -> str | None:
    """Read API auth token from the env var named in config, if configured."""
    token_env_var = get_value(f"api_services.{service_name}.auth_token_env")
    if not token_env_var:
        return None
    return os.getenv(token_env_var)

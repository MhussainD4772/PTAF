"""Unit tests for YAML reader and configuration accessors."""

from __future__ import annotations

import logging
from pathlib import Path

import pytest

from ptaf.utils import config, yaml_reader
from ptaf.utils.yaml_reader import _load_resources


class TestYamlReaderGet:
    def test_dot_path_returns_element_locator(self) -> None:
        assert (
            yaml_reader.get("elements.personal.first_name_field") == "CSS_#fname"
        )

    def test_missing_leaf_returns_none(self) -> None:
        assert yaml_reader.get("elements.personal.does_not_exist") is None

    def test_missing_intermediate_segment_returns_none(self) -> None:
        assert yaml_reader.get("elements.missing.personal") is None

    def test_non_dict_intermediate_returns_none(self) -> None:
        assert yaml_reader.get("browser.nested") is None


class TestYamlReaderLoad:
    def test_missing_folder_skipped_with_info_log(
        self, tmp_path: Path, caplog: pytest.LogCaptureFixture
    ) -> None:
        (tmp_path / "elements").mkdir()
        (tmp_path / "elements" / "sample.yml").write_text(
            "elements:\n  sample:\n    key: value\n",
            encoding="utf-8",
        )

        with caplog.at_level(logging.INFO):
            data = _load_resources(tmp_path)

        assert data == {"elements": {"sample": {"key": "value"}}}
        skipped = [
            record
            for record in caplog.records
            if "Configuration folder not found in resources, skipping"
            in record.message
        ]
        assert len(skipped) == 3
        skipped_names = {record.message.split(": ")[-1] for record in skipped}
        assert skipped_names == {"queries", "api_requests", "config"}


class TestConfigurationProperties:
    def test_get_browser(self) -> None:
        assert config.get_browser() == "chrome"

    def test_get_headless_mode(self) -> None:
        assert config.get_headless_mode() == "false"

    def test_get_base_url(self) -> None:
        assert (
            config.get_base_url("rnd_url")
            == "https://pdscawebdev01.myfnb.us/Account/Login?ReturnUrl=%2F"
        )

    def test_get_value_database_connection_url(self) -> None:
        assert (
            config.get_value("database.connection_url")
            == "jdbc:postgresql://localhost:5432/lime-of-time-database"
        )

    def test_get_value_password_env_variable(self) -> None:
        assert config.get_value("database.password_env_variable") == "DB_PASSWORD"

    def test_get_database_password_from_env(
        self, monkeypatch: pytest.MonkeyPatch
    ) -> None:
        monkeypatch.setenv("DB_PASSWORD", "secret-from-env")
        assert config.get_database_password() == "secret-from-env"

    def test_get_api_service_base_url(self) -> None:
        assert (
            config.get_api_service_base_url("jsonplaceholder")
            == "https://jsonplaceholder.typicode.com"
        )

    def test_get_api_service_auth_token_from_env(
        self, monkeypatch: pytest.MonkeyPatch
    ) -> None:
        monkeypatch.setenv("CRM_API_TOKEN", "token-from-env")
        assert config.get_api_service_auth_token("internal_crm") == "token-from-env"

    def test_get_api_service_auth_token_blank_env_name(self) -> None:
        assert config.get_api_service_auth_token("jsonplaceholder") is None

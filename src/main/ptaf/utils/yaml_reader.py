"""Load and query merged YAML configuration from resource folders."""

from __future__ import annotations

import logging
from pathlib import Path
from typing import Any

import yaml

logger = logging.getLogger(__name__)

_RESOURCES_ROOT = Path(__file__).resolve().parents[4] / "src" / "test" / "resources"
_FOLDER_PATHS = ("elements", "queries", "api_requests", "config")


def _merge_data(base: dict[str, Any], new_data: dict[str, Any]) -> None:
    for key, value in new_data.items():
        if (
            key in base
            and isinstance(base[key], dict)
            and isinstance(value, dict)
        ):
            _merge_data(base[key], value)
        else:
            base[key] = value


def _load_resources(resources_root: Path) -> dict[str, Any]:
    data: dict[str, Any] = {}

    for folder_name in _FOLDER_PATHS:
        folder_path = resources_root / folder_name
        if not folder_path.is_dir():
            logger.info(
                "Configuration folder not found in resources, skipping: %s",
                folder_name,
            )
            continue

        for path in folder_path.rglob("*"):
            if not path.is_file():
                continue
            if path.suffix not in (".yml", ".yaml"):
                continue
            try:
                with path.open(encoding="utf-8") as stream:
                    file_data = yaml.safe_load(stream)
                if file_data is not None:
                    _merge_data(data, file_data)
            except OSError:
                logger.exception("Error reading YAML file: %s", path)

    return data


_data: dict[str, Any] = _load_resources(_RESOURCES_ROOT)


def get(key: str) -> Any:
    """Retrieve a value using a dot-separated key path."""
    keys = key.split(".")
    current_map: dict[str, Any] = _data

    for part in keys[:-1]:
        value = current_map.get(part)
        if isinstance(value, dict):
            current_map = value
        else:
            return None

    return current_map.get(keys[-1])

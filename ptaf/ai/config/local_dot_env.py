"""Loads .env from the process working directory."""

from __future__ import annotations

import os
from pathlib import Path


def _load() -> dict[str, str]:
    env_path = Path(os.getcwd()).resolve() / ".env"
    if not env_path.is_file():
        return {}
    entries: dict[str, str] = {}
    for line in env_path.read_text(encoding="utf-8").splitlines():
        text = line.strip()
        if not text or text.startswith("#"):
            continue
        if "=" not in text:
            continue
        key, _, value = text.partition("=")
        key = key.strip()
        value = value.strip()
        if len(value) >= 2 and (
            (value.startswith('"') and value.endswith('"'))
            or (value.startswith("'") and value.endswith("'"))
        ):
            value = value[1:-1]
        if key:
            entries[key] = value
    return entries


_ENTRIES = _load()


def get(key: str | None) -> str | None:
    if key is None:
        return None
    return _ENTRIES.get(key)

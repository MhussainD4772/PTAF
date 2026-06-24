"""Indexes YAML keys across configured framework folders."""

from __future__ import annotations

from pathlib import Path
from typing import Any

import yaml


class YamlKeyIndex:
    def __init__(self, normalized_keys: set[str], key_source_type: dict[str, str]) -> None:
        self._normalized_keys = set(normalized_keys)
        self._key_source_type = dict(key_source_type)

    def normalized_keys(self) -> set[str]:
        return self._normalized_keys

    def key_source_type(self) -> dict[str, str]:
        return self._key_source_type

    @classmethod
    def build(cls, project_root: Path, yaml_paths: dict[str, str]) -> YamlKeyIndex:
        keys: set[str] = set()
        source_type: dict[str, str] = {}
        for namespace, relative_path in yaml_paths.items():
            root = (project_root / relative_path).resolve()
            if not root.is_dir():
                continue
            for file_path in sorted(root.rglob("*")):
                if not file_path.is_file():
                    continue
                name = file_path.name.lower()
                if not (name.endswith(".yml") or name.endswith(".yaml")):
                    continue
                parsed = yaml.safe_load(file_path.read_text(encoding="utf-8"))
                cls._flatten(namespace, "", parsed, keys, source_type)
        return cls(keys, source_type)

    @staticmethod
    def normalize_key(raw: str | None) -> str:
        if raw is None:
            return ""
        normalized = raw.strip().lower()
        while ".." in normalized:
            normalized = normalized.replace("..", ".")
        normalized = normalized.strip(".")
        return normalized

    @classmethod
    def _flatten(
        cls,
        namespace: str,
        current_path: str,
        value: Any,
        keys: set[str],
        source_type: dict[str, str],
    ) -> None:
        if isinstance(value, dict):
            for key, child in value.items():
                next_path = key if not current_path else f"{current_path}.{key}"
                full_key = cls.normalize_key(f"{namespace}.{next_path}")
                keys.add(full_key)
                source_type.setdefault(full_key, namespace)
                cls._flatten(namespace, next_path, child, keys, source_type)
            return
        if isinstance(value, list):
            for index, child in enumerate(value):
                next_path = f"{current_path}.{index}"
                full_key = cls.normalize_key(f"{namespace}.{next_path}")
                keys.add(full_key)
                source_type.setdefault(full_key, namespace)
                cls._flatten(namespace, next_path, child, keys, source_type)

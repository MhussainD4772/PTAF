from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class YamlKeyValidationResult:
    yaml_keys_used: list[str]
    existing_keys: list[str]
    missing_keys: list[str]
    suggested_patches: dict[str, str]
    total_keys: int
    existing_count: int
    missing_count: int
    passed: bool
    warnings: list[str]

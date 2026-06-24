from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class AllowedYamlGuardResult:
    passed: bool
    allowed_keys: list[str]
    unknown_keys_used: list[str]
    missing_keys_declared: list[str]
    missing_keys_used_in_feature: list[str]
    warnings: list[str]
    blocking_errors: list[str]

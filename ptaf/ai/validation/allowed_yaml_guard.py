from __future__ import annotations

import re

from ptaf.ai.index.yaml_key_index import YamlKeyIndex
from ptaf.ai.model.ai_generation_structured_response import AiGenerationStructuredResponse
from ptaf.ai.validation.allowed_yaml_guard_result import AllowedYamlGuardResult

_YAML_KEY_PATTERN = re.compile(r"(elements|api_requests|queries|config)\.[a-zA-Z0-9_.-]+")


class AllowedYamlGuard:
    def validate(
        self,
        structured_response: AiGenerationStructuredResponse,
        yaml_key_index: YamlKeyIndex,
    ) -> AllowedYamlGuardResult:
        known = yaml_key_index.normalized_keys()
        used = _normalize_list(structured_response.yaml_keys_used)
        missing_declared = _normalize_list(structured_response.missing_yaml_keys)
        keys_in_feature = _extract_yaml_like_keys(structured_response.feature_file)

        allowed: list[str] = []
        unknown: list[str] = []
        warnings: list[str] = []
        blocking: list[str] = []
        missing_used_in_feature: list[str] = []

        for key in used:
            if key in known:
                allowed.append(key)
            else:
                unknown.append(key)
                blocking.append(f"YAML key does not exist: {key}")

        for key in missing_declared:
            if key in keys_in_feature:
                missing_used_in_feature.append(key)
                blocking.append(f"Missing YAML key cannot be used directly in FEATURE_FILE: {key}")

        for key in keys_in_feature:
            is_known = key in known
            listed_used = key in used
            if not is_known:
                if key not in unknown:
                    unknown.append(key)
                blocking.append(f"Unknown YAML-looking key used in FEATURE_FILE: {key}")
            elif not listed_used:
                warnings.append(
                    f"Known YAML key appears in FEATURE_FILE but not listed in YAML_KEYS_USED: {key}"
                )

        return AllowedYamlGuardResult(
            passed=not blocking,
            allowed_keys=allowed,
            unknown_keys_used=unknown,
            missing_keys_declared=list(missing_declared),
            missing_keys_used_in_feature=missing_used_in_feature,
            warnings=_dedupe(warnings),
            blocking_errors=_dedupe(blocking),
        )


def _normalize_list(keys: list[str] | None) -> set[str]:
    out: set[str] = set()
    if not keys:
        return out
    for key in keys:
        normalized = YamlKeyIndex.normalize_key(key)
        if normalized:
            out.add(normalized)
    return out


def _extract_yaml_like_keys(feature_file: str | None) -> set[str]:
    out: set[str] = set()
    if not feature_file or not feature_file.strip():
        return out
    for match in _YAML_KEY_PATTERN.finditer(feature_file):
        key = YamlKeyIndex.normalize_key(match.group())
        if key:
            out.add(key)
    return out


def _dedupe(values: list[str]) -> list[str]:
    seen: set[str] = set()
    out: list[str] = []
    for value in values:
        if value not in seen:
            seen.add(value)
            out.append(value)
    return out

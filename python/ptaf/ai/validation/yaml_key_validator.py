from __future__ import annotations

from ptaf.ai.index.yaml_key_index import YamlKeyIndex
from ptaf.ai.model.ai_generation_structured_response import AiGenerationStructuredResponse
from ptaf.ai.validation.yaml_key_validation_result import YamlKeyValidationResult


class YamlKeyValidator:
    def validate(
        self,
        structured_response: AiGenerationStructuredResponse,
        yaml_key_index: YamlKeyIndex,
    ) -> YamlKeyValidationResult:
        used_normalized: list[str] = []
        seen: set[str] = set()
        for key in structured_response.yaml_keys_used:
            normalized = YamlKeyIndex.normalize_key(key)
            if normalized and normalized not in seen:
                seen.add(normalized)
                used_normalized.append(normalized)

        existing: list[str] = []
        missing: list[str] = []
        for used in used_normalized:
            if used in yaml_key_index.normalized_keys():
                existing.append(used)
            else:
                missing.append(used)

        ai_missing_normalized: set[str] = set()
        for key in structured_response.missing_yaml_keys:
            normalized = YamlKeyIndex.normalize_key(key)
            if normalized:
                ai_missing_normalized.add(normalized)

        warnings: list[str] = []
        for ai_missing in ai_missing_normalized:
            if ai_missing in yaml_key_index.normalized_keys():
                warnings.append(f"AI reported missing key that exists: {ai_missing}")
        for actually_missing in missing:
            if actually_missing not in ai_missing_normalized:
                warnings.append(f"AI missed missing YAML key: {actually_missing}")

        suggested_patches = {missing_key: _build_patch_suggestion(missing_key) for missing_key in missing}
        total = len(used_normalized)
        existing_count = len(existing)
        missing_count = len(missing)
        passed = missing_count == 0

        return YamlKeyValidationResult(
            yaml_keys_used=list(used_normalized),
            existing_keys=existing,
            missing_keys=missing,
            suggested_patches=suggested_patches,
            total_keys=total,
            existing_count=existing_count,
            missing_count=missing_count,
            passed=passed,
            warnings=warnings,
        )


def _build_patch_suggestion(key: str) -> str:
    parts = key.split(".")
    if len(parts) < 2:
        return "TODO_VALUE"
    namespace = parts[0].lower()
    path = parts[1:]
    if namespace == "elements":
        return _build_leaf_patch(path, '"TODO_SELECTOR"')
    if namespace == "api_requests":
        return _build_api_patch(path)
    if namespace == "queries":
        return _build_leaf_patch(path, '"TODO_SQL_QUERY"')
    return _build_leaf_patch(path, '"TODO_VALUE"')


def _build_leaf_patch(path: list[str], leaf_value: str) -> str:
    if not path:
        return 'method: "TODO_METHOD"\npath: "TODO_PATH"\nheaders: {}\nbody: {}'
    lines: list[str] = []
    for index, segment in enumerate(path):
        indent = "  " * index
        if index == len(path) - 1:
            lines.append(f"{indent}{segment}: {leaf_value}")
        else:
            lines.append(f"{indent}{segment}:")
    return "\n".join(lines).strip()


def _build_api_patch(path: list[str]) -> str:
    if not path:
        return 'method: "TODO_METHOD"\npath: "TODO_PATH"\nheaders: {}\nbody: {}'
    lines: list[str] = []
    for index, segment in enumerate(path):
        indent = "  " * index
        if index == len(path) - 1:
            lines.append(f"{indent}{segment}:")
            inner = "  " * (index + 1)
            lines.extend(
                [
                    f'{inner}method: "TODO_METHOD"',
                    f'{inner}path: "TODO_PATH"',
                    f"{inner}headers: {{}}",
                    f"{inner}body: {{}}",
                ]
            )
        else:
            lines.append(f"{indent}{segment}:")
    return "\n".join(lines).strip()

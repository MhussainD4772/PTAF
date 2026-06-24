from __future__ import annotations

from ptaf.ai.index.yaml_key_index import YamlKeyIndex
from ptaf.ai.validation.allowed_yaml_guard_result import AllowedYamlGuardResult
from ptaf.ai.validation.missing_yaml_patch_suggestion import MissingYamlPatchSuggestion
from ptaf.ai.validation.yaml_key_validation_result import YamlKeyValidationResult


class MissingYamlPatchSuggester:
    def suggest(
        self,
        yaml_key_validation_result: YamlKeyValidationResult | None,
        allowed_yaml_guard_result: AllowedYamlGuardResult | None,
    ) -> list[MissingYamlPatchSuggestion]:
        missing_keys: set[str] = set()
        if yaml_key_validation_result:
            missing_keys.update(yaml_key_validation_result.missing_keys)
        if allowed_yaml_guard_result:
            missing_keys.update(allowed_yaml_guard_result.unknown_keys_used)
            missing_keys.update(allowed_yaml_guard_result.missing_keys_used_in_feature)

        out: list[MissingYamlPatchSuggestion] = []
        for raw_key in missing_keys:
            key = YamlKeyIndex.normalize_key(raw_key)
            if key:
                out.append(_build_suggestion(key))
        return out


def _build_suggestion(key: str) -> MissingYamlPatchSuggestion:
    parts = key.split(".")
    if len(parts) < 2:
        return MissingYamlPatchSuggestion(
            missing_key=key,
            category="unknown",
            target_folder="unknown",
            suggested_yaml="TODO_VALUE",
            warnings=["Unknown YAML key category; manual patch required"],
        )

    category = parts[0]
    path = parts[1:]
    mapping = {
        "elements": ("elements", "src/test/resources/elements", _build_leaf_patch(path, '"TODO_SELECTOR"')),
        "api_requests": ("api_requests", "src/test/resources/api_requests", _build_api_patch(path)),
        "queries": ("queries", "src/test/resources/queries", _build_leaf_patch(path, '"TODO_SQL_QUERY"')),
        "config": ("config", "src/test/resources/config", _build_leaf_patch(path, '"TODO_VALUE"')),
    }
    if category in mapping:
        cat, folder, patch = mapping[category]
        return MissingYamlPatchSuggestion(
            missing_key=key,
            category=cat,
            target_folder=folder,
            suggested_yaml=patch,
            warnings=[],
        )
    return MissingYamlPatchSuggestion(
        missing_key=key,
        category=category,
        target_folder="unknown",
        suggested_yaml=_build_leaf_patch(path, '"TODO_VALUE"'),
        warnings=["Unknown YAML key category; verify target folder manually"],
    )


def _build_leaf_patch(path: list[str], leaf_value: str) -> str:
    if not path:
        return f"value: {leaf_value}"
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

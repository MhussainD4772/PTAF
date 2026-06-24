from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class FrameworkGenerationContext:
    existing_feature_snippets: list[str]
    existing_step_definitions: list[str]
    existing_yaml_keys: list[str]
    ui_element_keys: list[str]
    api_request_keys: list[str]
    db_query_keys: list[str]

    @property
    def feature_snippet_count(self) -> int:
        return len(self.existing_feature_snippets)

    @property
    def step_definition_count(self) -> int:
        return len(self.existing_step_definitions)

    @property
    def yaml_key_count(self) -> int:
        return len(self.existing_yaml_keys)

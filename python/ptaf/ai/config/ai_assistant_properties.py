"""Loads ai_assistant.yml from python/resources/config/."""

from __future__ import annotations

import os
from pathlib import Path
from typing import Any

import yaml

from ptaf.ai.config.local_dot_env import get as dotenv_get


def _package_root() -> Path:
    return Path(__file__).resolve().parents[3]


def _load_yaml() -> dict[str, Any]:
    path = _package_root() / "resources" / "config" / "ai_assistant.yml"
    if not path.is_file():
        return {}
    with path.open(encoding="utf-8") as handle:
        data = yaml.safe_load(handle)
    return data if isinstance(data, dict) else {}


class AiAssistantProperties:
    def __init__(self, root: dict[str, Any] | None = None) -> None:
        self._root = root if root is not None else _load_yaml()

    def _ai(self) -> dict[str, Any]:
        value = self._root.get("ai_assistant")
        return value if isinstance(value, dict) else {}

    def _context(self) -> dict[str, Any]:
        value = self._ai().get("context")
        return value if isinstance(value, dict) else {}

    def _context_rules(self) -> dict[str, Any]:
        value = self._ai().get("contextRules")
        return value if isinstance(value, dict) else {}

    def _str(self, key: str, default: str) -> str:
        value = self._ai().get(key)
        return str(value) if value is not None else default

    def _dbl(self, key: str, default: float) -> float:
        value = self._ai().get(key)
        if isinstance(value, (int, float)):
            return float(value)
        if isinstance(value, str):
            try:
                return float(value)
            except ValueError:
                return default
        return default

    def _integer(self, key: str, default: int) -> int:
        value = self._ai().get(key)
        if isinstance(value, int):
            return value
        if isinstance(value, float):
            return int(value)
        if isinstance(value, str):
            try:
                return int(value)
            except ValueError:
                return default
        return default

    def gemini_api_key_env_name(self) -> str:
        return self._str("gemini_api_key_env", "GEMINI_API_KEY")

    def api_key(self) -> str:
        name = self.gemini_api_key_env_name()
        value = os.environ.get(name)
        if value and value.strip():
            return value.strip()
        from_file = dotenv_get(name)
        return from_file.strip() if from_file else ""

    def model(self) -> str:
        for source in (os.environ.get("GEMINI_MODEL"), dotenv_get("GEMINI_MODEL")):
            if source and source.strip():
                return source.strip()
        return self._str("model", "gemini-2.5-flash")

    def features_dir(self) -> str:
        return self._str("features_dir", "features")

    def step_definitions_dir(self) -> str:
        return self._str("step_definitions_dir", "steps")

    def step_definition_paths(self) -> list[str]:
        value = self._ai().get("step_definition_paths")
        if isinstance(value, list):
            return [str(item).strip() for item in value if item and str(item).strip()]
        return [self.step_definitions_dir()]

    def context_step_definition_paths(self) -> list[str]:
        value = self._context().get("stepDefinitionPaths")
        if isinstance(value, list):
            return [str(item).strip() for item in value if item and str(item).strip()]
        return self.step_definition_paths()

    def yaml_paths(self) -> dict[str, str]:
        defaults = {
            "elements": "resources/elements",
            "api_requests": "resources/api_requests",
            "queries": "resources/queries",
            "config": "resources/config",
        }
        value = self._ai().get("yaml_paths")
        if not isinstance(value, dict):
            return defaults
        out = dict(defaults)
        for key, path in value.items():
            key_str = str(key).strip()
            path_str = str(path).strip()
            if key_str and path_str:
                out[key_str] = path_str
        return out

    def context_yaml_paths(self) -> dict[str, str]:
        value = self._context().get("yamlPaths")
        if not isinstance(value, dict):
            return self.yaml_paths()
        out = dict(self.yaml_paths())
        for key, path in value.items():
            key_str = str(key).strip()
            path_str = str(path).strip()
            if key_str and path_str:
                out[key_str] = path_str
        return out

    def context_feature_paths(self) -> list[str]:
        value = self._context().get("featurePaths")
        if isinstance(value, list):
            return [str(item).strip() for item in value if item and str(item).strip()]
        return [self.features_dir()]

    def context_max_feature_snippets(self) -> int:
        return self._context_int("maxFeatureSnippets", 20)

    def context_max_step_definitions_in_prompt(self) -> int:
        return self._context_int("maxStepDefinitionsInPrompt", 100)

    def context_max_yaml_keys_in_prompt(self) -> int:
        return self._context_int("maxYamlKeysInPrompt", 200)

    def context_max_similar_features(self) -> int:
        return self._context_int("maxSimilarFeatures", 3)

    def context_min_similarity_score(self) -> int:
        return self._context_int("minSimilarityScore", 1)

    def _context_int(self, key: str, default: int) -> int:
        value = self._context().get(key)
        if isinstance(value, int):
            return value
        if isinstance(value, str):
            try:
                return int(value)
            except ValueError:
                return default
        return default

    def default_ui_context(self) -> str:
        value = self._context_rules().get("defaultUiContext")
        out = str(value).strip().lower() if value is not None else "page"
        return out or "page"

    def frame_allowed_pages(self) -> list[str]:
        return self._context_string_list("frameAllowedPages")

    def frame_allowed_locators(self) -> list[str]:
        return self._context_string_list("frameAllowedLocators")

    def _context_string_list(self, key: str) -> list[str]:
        value = self._context_rules().get(key)
        if not isinstance(value, list):
            return []
        return [str(item).strip().lower() for item in value if item and str(item).strip()]

    def max_feature_files(self) -> int:
        return self._integer("max_feature_files", 8)

    def max_step_def_files(self) -> int:
        return self._integer("max_step_def_files", 8)

    def hooks_dir(self) -> str:
        return self._str("hooks_dir", "ptaf")

    def ui_pages_dir(self) -> str:
        return self._str("ui_pages_dir", "ptaf/ui")

    def elements_dir(self) -> str:
        return self._str("elements_dir", "resources/elements")

    def config_yaml_dir(self) -> str:
        return self._str("config_yaml_dir", "resources/config")

    def max_hooks_files(self) -> int:
        return self._integer("max_hooks_files", 8)

    def max_pages_files(self) -> int:
        return self._integer("max_pages_files", 16)

    def max_elements_files(self) -> int:
        return self._integer("max_elements_files", 20)

    def max_config_yaml_files(self) -> int:
        return self._integer("max_config_yaml_files", 10)

    def ranking_top_chunks(self) -> int:
        return self._integer("ranking_top_chunks", 14)

    def ranking_top_patterns(self) -> int:
        return self._integer("ranking_top_patterns", 55)

    def max_total_context_chars(self) -> int:
        return self._integer("max_total_context_chars", 100_000)

    def temperature(self) -> float:
        return self._dbl("temperature", 0.2)

    def max_output_tokens(self) -> int:
        return self._integer("max_output_tokens", 8192)

    def prompt_version(self) -> str:
        return self._str("prompt_version", "phase1-v1")

    def audit_enabled(self) -> bool:
        audit = self._ai().get("audit")
        if isinstance(audit, dict):
            enabled = audit.get("enabled")
            if isinstance(enabled, bool):
                return enabled
            if isinstance(enabled, str):
                return enabled.strip().lower() in {"true", "1", "yes"}
        return True

    def audit_output_path(self) -> str:
        audit = self._ai().get("audit")
        if isinstance(audit, dict):
            output = audit.get("output_path")
            if output and str(output).strip():
                return str(output).strip()
        return "target/ai-audit/generation-audit.jsonl"

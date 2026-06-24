from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class MissingYamlPatchSuggestion:
    missing_key: str
    category: str
    target_folder: str
    suggested_yaml: str
    warnings: list[str]

from __future__ import annotations

from dataclasses import dataclass, field


@dataclass
class AiGenerationStructuredResponse:
    feature_file: str = ""
    reused_steps: list[str] = field(default_factory=list)
    new_steps_needed: list[str] = field(default_factory=list)
    yaml_keys_used: list[str] = field(default_factory=list)
    missing_yaml_keys: list[str] = field(default_factory=list)
    warnings: list[str] = field(default_factory=list)
    parse_successful: bool = False
    parse_errors: list[str] = field(default_factory=list)

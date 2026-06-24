from __future__ import annotations

import re
from pathlib import Path
from typing import Any

import yaml

from ptaf.ai.security.log_redactor import redact


class AiPolicy:
    def __init__(self, root: dict[str, Any] | None = None) -> None:
        self._root = root if root is not None else self._load()

    @staticmethod
    def _load() -> dict[str, Any]:
        path = Path(__file__).resolve().parents[5] / "src" / "test" / "resources" / "config" / "ai_policy.yml"
        if not path.is_file():
            return {}
        with path.open(encoding="utf-8") as handle:
            data = yaml.safe_load(handle)
        return data if isinstance(data, dict) else {}

    def _policy(self) -> dict[str, Any]:
        value = self._root.get("ai_policy")
        return value if isinstance(value, dict) else {}

    def max_requirement_chars(self) -> int:
        value = self._policy().get("max_requirement_chars")
        return int(value) if isinstance(value, (int, float)) else 20_000

    def duplicate_step_warn_threshold(self) -> int:
        value = self._policy().get("duplicate_step_warn_threshold")
        return int(value) if isinstance(value, (int, float)) else 8

    def duplicate_step_fail_threshold(self) -> int:
        value = self._policy().get("duplicate_step_fail_threshold")
        return int(value) if isinstance(value, (int, float)) else 25

    def redact_triage_input(self) -> bool:
        value = self._policy().get("redact_triage_input")
        return bool(value) if isinstance(value, bool) else True

    def maybe_redact_triage_input(self, text: str | None) -> str | None:
        if text is None:
            return None
        return redact(text) if self.redact_triage_input() else text

    def blocked_requirement_patterns(self) -> list[re.Pattern[str]]:
        value = self._policy().get("blocked_requirement_patterns")
        if not isinstance(value, list):
            return []
        patterns: list[re.Pattern[str]] = []
        for item in value:
            if item is None:
                continue
            try:
                patterns.append(re.compile(str(item)))
            except re.error:
                continue
        return patterns

    def validate_requirement(self, requirement: str | None) -> str | None:
        if requirement is None:
            return "requirement is null"
        max_chars = self.max_requirement_chars()
        if max_chars > 0 and len(requirement) > max_chars:
            return f"requirement exceeds max_requirement_chars ({max_chars})"
        for pattern in self.blocked_requirement_patterns():
            if pattern.search(requirement):
                return f"requirement matched blocked pattern: {pattern.pattern}"
        return None

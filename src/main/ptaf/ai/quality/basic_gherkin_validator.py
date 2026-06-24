from __future__ import annotations

import re

_FEATURE = re.compile(r"^\s*Feature:\s*\S+", re.IGNORECASE)
_SCENARIO = re.compile(r"^\s*(Scenario|Scenario Outline):\s*\S+", re.IGNORECASE)


def validate(relative_path: str, content: str) -> list[str]:
    issues: list[str] = []
    has_feature = False
    has_scenario = False
    for line in content.splitlines():
        if _FEATURE.search(line):
            has_feature = True
        if _SCENARIO.search(line):
            has_scenario = True
    if not has_feature:
        issues.append(f"{relative_path}: missing Feature: line")
    if not has_scenario:
        issues.append(f"{relative_path}: missing Scenario / Scenario Outline")
    return issues

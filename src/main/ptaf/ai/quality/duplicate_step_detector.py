from __future__ import annotations

import re
from dataclasses import dataclass

_STEP_LINE = re.compile(r"^\s*(Given|When|Then|And|But)\s+(.+?)\s*$", re.IGNORECASE)


@dataclass(frozen=True)
class StepOccurrence:
    relative_path: str
    line_number: int
    raw_line: str
    normalized: str


@dataclass(frozen=True)
class DuplicateGroup:
    normalized_step: str
    occurrences: list[StepOccurrence]

    @property
    def count(self) -> int:
        return len(self.occurrences)


def normalize(step_body: str | None) -> str:
    if step_body is None:
        return ""
    return " ".join(step_body.strip().lower().split())


def scan_file(relative_path: str, lines: list[str]) -> list[StepOccurrence]:
    out: list[StepOccurrence] = []
    for index, line in enumerate(lines):
        match = _STEP_LINE.match(line)
        if not match:
            continue
        body = match.group(2)
        norm = normalize(body)
        if norm:
            out.append(StepOccurrence(relative_path, index + 1, line.strip(), norm))
    return out


def find_duplicates(all_steps: list[StepOccurrence], warn_threshold: int) -> list[DuplicateGroup]:
    by_norm: dict[str, list[StepOccurrence]] = {}
    for occurrence in all_steps:
        by_norm.setdefault(occurrence.normalized, []).append(occurrence)
    groups = [
        DuplicateGroup(normalized, occurrences)
        for normalized, occurrences in by_norm.items()
        if len(occurrences) >= warn_threshold
    ]
    groups.sort(key=lambda group: len(group.occurrences), reverse=True)
    return groups

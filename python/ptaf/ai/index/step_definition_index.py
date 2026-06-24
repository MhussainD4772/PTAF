"""Index of known step definition patterns extracted from Python step files."""

from __future__ import annotations

from pathlib import Path

from ptaf.ai.step_pattern_extractor import from_python_source


class StepDefinitionIndex:
    def __init__(self, known_steps: list[str]) -> None:
        self._known_steps = list(known_steps)

    def known_steps(self) -> list[str]:
        return self._known_steps

    @classmethod
    def build(cls, project_root: Path, step_definition_paths: list[str]) -> StepDefinitionIndex:
        ordered: list[str] = []
        seen: set[str] = set()
        for relative_path in step_definition_paths:
            if not relative_path or not relative_path.strip():
                continue
            root = (project_root / relative_path).resolve()
            if not root.is_dir():
                continue
            for py_file in sorted(root.rglob("*.py")):
                if not py_file.is_file():
                    continue
                source = py_file.read_text(encoding="utf-8")
                for pattern in from_python_source(source):
                    if pattern not in seen:
                        seen.add(pattern)
                        ordered.append(pattern)
        return cls(ordered)

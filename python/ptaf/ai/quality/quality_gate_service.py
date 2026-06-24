from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path

from ptaf.ai.policy.ai_policy import AiPolicy
from ptaf.ai.quality.basic_gherkin_validator import validate as validate_gherkin
from ptaf.ai.quality.duplicate_step_detector import DuplicateGroup, find_duplicates, scan_file


@dataclass(frozen=True)
class QualityReport:
    syntax_issues: list[str]
    duplicate_groups: list[DuplicateGroup]
    warn_threshold: int
    failed_strict: bool

    @property
    def has_errors(self) -> bool:
        return self.failed_strict or bool(self.syntax_issues)


class QualityGateService:
    def __init__(self, policy: AiPolicy | None = None) -> None:
        self._policy = policy or AiPolicy()

    def run(self, project_root: Path, features_relative_dir: str, strict: bool) -> QualityReport:
        project_root = project_root.resolve()
        root = (project_root / features_relative_dir).resolve()
        syntax_issues: list[str] = []
        all_steps = []

        if not root.is_dir():
            syntax_issues.append(f"Features directory not found: {features_relative_dir}")
            failed = strict and bool(syntax_issues)
            return QualityReport(syntax_issues, [], self._policy.duplicate_step_warn_threshold(), failed)

        features = sorted(root.rglob("*.feature"))
        for feature_path in features:
            if not feature_path.is_file():
                continue
            rel = feature_path.relative_to(project_root).as_posix()
            content = feature_path.read_text(encoding="utf-8")
            syntax_issues.extend(validate_gherkin(rel, content))
            lines = content.splitlines()
            all_steps.extend(scan_file(rel, lines))

        warn = self._policy.duplicate_step_warn_threshold()
        fail_threshold = self._policy.duplicate_step_fail_threshold()
        duplicates = find_duplicates(all_steps, warn)
        should_fail = strict and (
            bool(syntax_issues) or any(group.count >= fail_threshold for group in duplicates)
        )
        return QualityReport(syntax_issues, duplicates, warn, should_fail)

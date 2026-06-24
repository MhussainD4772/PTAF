from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class StepReuseValidationResult:
    feature_steps: list[str]
    matched_existing_steps: list[str]
    unmatched_steps: list[str]
    claimed_reused_but_not_found: list[str]
    claimed_new_but_already_exists: list[str]
    total_steps: int
    matched_count: int
    unmatched_count: int
    reuse_percentage: float
    passed: bool
    warnings: list[str]

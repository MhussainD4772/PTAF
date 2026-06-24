from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class RunnableFeatureResult:
    runnable: bool
    blocking_reasons: list[str]
    warnings: list[str]
    parse_successful: bool
    step_validation_passed: bool
    yaml_validation_passed: bool
    allowed_yaml_passed: bool
    page_frame_context_passed: bool
    step_reuse_percentage: float
    total_steps: int
    matched_steps: int
    unmatched_steps: int
    yaml_keys_used: int
    existing_yaml_keys: int
    missing_yaml_keys: int
    frame_step_count: int
    page_step_count: int

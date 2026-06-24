from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class PageFrameContextGuardResult:
    passed: bool
    invalid_frame_steps: list[str]
    invalid_page_steps: list[str]
    warnings: list[str]
    blocking_errors: list[str]
    frame_step_count: int
    page_step_count: int

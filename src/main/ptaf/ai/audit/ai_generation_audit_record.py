from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class AiGenerationAuditRecord:
    request_id: str
    timestamp: str
    command: str
    mode: str
    model: str
    prompt_version: str
    requirement_hash: str
    output_path: str
    parse_successful: bool
    step_validation_passed: bool
    yaml_validation_passed: bool
    file_written: bool
    reused_steps_count: int
    new_steps_count: int
    missing_yaml_keys_count: int
    warnings: list[str]
    blocking_errors: list[str]

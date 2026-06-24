from __future__ import annotations

from datetime import datetime, timezone
from pathlib import Path
from uuid import uuid4

from ptaf.ai.audit.ai_generation_audit_logger import AiGenerationAuditLogger
from ptaf.ai.audit.ai_generation_audit_record import AiGenerationAuditRecord
from ptaf.ai.config.ai_assistant_properties import AiAssistantProperties
from ptaf.ai.model.ai_generation_mode import AiGenerationMode
from ptaf.ai.model.generation_result import GenerationResult


def collect_warnings(result: GenerationResult | None) -> list[str]:
    if result is None:
        return []
    warnings: list[str] = []
    warnings.extend(result.structured_response.warnings)
    if result.step_reuse_validation_result:
        warnings.extend(result.step_reuse_validation_result.warnings)
    if result.yaml_key_validation_result:
        warnings.extend(result.yaml_key_validation_result.warnings)
    if result.allowed_yaml_guard_result:
        warnings.extend(result.allowed_yaml_guard_result.warnings)
    if result.page_frame_context_guard_result:
        warnings.extend(result.page_frame_context_guard_result.warnings)
    if result.runnable_feature_result:
        warnings.extend(result.runnable_feature_result.warnings)
    if result.missing_yaml_patch_suggestions:
        for suggestion in result.missing_yaml_patch_suggestions:
            warnings.extend(suggestion.warnings)
    return warnings


def build_record(
    mode: AiGenerationMode,
    props: AiAssistantProperties,
    requirement: str,
    requested_output: Path,
    written_output: Path | None,
    result: GenerationResult | None,
    blocking_errors: list[str],
    warnings: list[str],
) -> AiGenerationAuditRecord:
    parse_ok = result is not None and result.structured_response.parse_successful
    step_ok = (
        result is not None
        and result.step_reuse_validation_result is not None
        and result.step_reuse_validation_result.passed
    )
    yaml_ok = (
        result is not None
        and result.yaml_key_validation_result is not None
        and result.yaml_key_validation_result.passed
    )
    reused = len(result.structured_response.reused_steps) if result else 0
    new_steps = len(result.structured_response.new_steps_needed) if result else 0
    missing_yaml = result.yaml_key_validation_result.missing_count if result and result.yaml_key_validation_result else 0
    output_path = str(written_output) if written_output else str(requested_output)

    return AiGenerationAuditRecord(
        request_id=str(uuid4()),
        timestamp=datetime.now(timezone.utc).isoformat(),
        command="generate",
        mode=mode.name,
        model=props.model(),
        prompt_version=props.prompt_version(),
        requirement_hash=AiGenerationAuditLogger.sha256(requirement),
        output_path=output_path,
        parse_successful=parse_ok,
        step_validation_passed=step_ok,
        yaml_validation_passed=yaml_ok,
        file_written=written_output is not None,
        reused_steps_count=reused,
        new_steps_count=new_steps,
        missing_yaml_keys_count=missing_yaml,
        warnings=warnings,
        blocking_errors=blocking_errors,
    )


def append(
    project_root: Path,
    props: AiAssistantProperties,
    mode: AiGenerationMode,
    requirement: str,
    requested_output: Path,
    written_output: Path | None,
    result: GenerationResult | None,
    blocking_errors: list[str],
) -> AiGenerationAuditLogger.WriteResult:
    warnings = collect_warnings(result)
    record = build_record(
        mode,
        props,
        requirement,
        requested_output,
        written_output,
        result,
        blocking_errors,
        warnings,
    )
    return AiGenerationAuditLogger().append(
        project_root,
        props.audit_enabled(),
        props.audit_output_path(),
        record,
    )

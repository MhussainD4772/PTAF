from __future__ import annotations

from ptaf.ai.model.ai_generation_mode import AiGenerationMode
from ptaf.ai.model.generation_result import GenerationResult


class GenerationModeEvaluator:
    def blocking_errors(self, mode: AiGenerationMode, result: GenerationResult) -> list[str]:
        errors: list[str] = []
        structured = result.structured_response
        step = result.step_reuse_validation_result
        yaml = result.yaml_key_validation_result
        guard = result.allowed_yaml_guard_result
        runnable = result.runnable_feature_result

        if not structured.parse_successful:
            errors.append("Structured parse failed")
            for parse_error in structured.parse_errors:
                errors.append(f"Parse error: {parse_error}")
        if not result.feature_gherkin or not result.feature_gherkin.strip():
            errors.append("Generated feature is empty")
        if runnable is not None and not runnable.runnable:
            errors.extend(runnable.blocking_reasons)
        elif guard is not None and guard.blocking_errors:
            errors.extend(guard.blocking_errors)

        if mode == AiGenerationMode.STRICT:
            if yaml is not None and yaml.missing_keys:
                for key in yaml.missing_keys:
                    errors.append(f"Missing YAML key: {key}")
            if step is not None and step.claimed_reused_but_not_found:
                for step_text in step.claimed_reused_but_not_found:
                    errors.append(f"Reused step claimed but not found: {step_text}")
            if step is not None and not step.passed:
                errors.append(
                    "One or more feature steps are unmatched and not listed under NEW_STEPS_NEEDED"
                )

        return errors

    def should_write_file(self, mode: AiGenerationMode, blocking_errors: list[str]) -> bool:
        return mode == AiGenerationMode.WRITE and not blocking_errors

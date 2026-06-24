from __future__ import annotations

from ptaf.ai.model.ai_generation_structured_response import AiGenerationStructuredResponse
from ptaf.ai.validation.allowed_yaml_guard_result import AllowedYamlGuardResult
from ptaf.ai.validation.page_frame_context_guard_result import PageFrameContextGuardResult
from ptaf.ai.validation.runnable_feature_result import RunnableFeatureResult
from ptaf.ai.validation.step_reuse_validation_result import StepReuseValidationResult
from ptaf.ai.validation.yaml_key_validation_result import YamlKeyValidationResult


class RunnableFeatureGate:
    def evaluate(
        self,
        structured_response: AiGenerationStructuredResponse | None,
        step_reuse_result: StepReuseValidationResult | None,
        yaml_key_result: YamlKeyValidationResult | None,
        allowed_yaml_guard_result: AllowedYamlGuardResult | None,
        page_frame_context_guard_result: PageFrameContextGuardResult | None,
    ) -> RunnableFeatureResult:
        blocking: list[str] = []
        warnings: list[str] = []

        parse_successful = structured_response is not None and structured_response.parse_successful
        feature_empty = (
            structured_response is None
            or not structured_response.feature_file
            or not structured_response.feature_file.strip()
        )
        step_validation_passed = step_reuse_result is not None and step_reuse_result.passed
        yaml_validation_passed = yaml_key_result is not None and yaml_key_result.passed
        allowed_yaml_passed = allowed_yaml_guard_result is not None and allowed_yaml_guard_result.passed
        page_frame_context_passed = (
            page_frame_context_guard_result is not None and page_frame_context_guard_result.passed
        )

        if not parse_successful:
            blocking.append("Structured AI response failed to parse")
        if feature_empty:
            blocking.append("Generated feature file is empty")
        if not step_validation_passed:
            blocking.append("Step validation failed")
            if step_reuse_result:
                for step in step_reuse_result.unmatched_steps:
                    blocking.append(
                        f"Feature step is not reusable and not declared as new: {step}"
                    )
        if not yaml_validation_passed:
            blocking.append("YAML validation failed")
        if not allowed_yaml_passed:
            blocking.append("Allowed YAML guard failed")
            if allowed_yaml_guard_result:
                for key in allowed_yaml_guard_result.unknown_keys_used:
                    blocking.append(f"Unknown YAML key used: {key}")
                for key in allowed_yaml_guard_result.missing_keys_used_in_feature:
                    blocking.append(f"Missing YAML key appears inside feature file: {key}")
                blocking.extend(allowed_yaml_guard_result.blocking_errors)
        if not page_frame_context_passed:
            blocking.append("Page/frame context guard failed")
            if page_frame_context_guard_result:
                blocking.extend(page_frame_context_guard_result.blocking_errors)

        if step_reuse_result:
            warnings.extend(step_reuse_result.warnings)
        if yaml_key_result:
            warnings.extend(yaml_key_result.warnings)
        if allowed_yaml_guard_result:
            warnings.extend(allowed_yaml_guard_result.warnings)
        if page_frame_context_guard_result:
            warnings.extend(page_frame_context_guard_result.warnings)

        unique_blocking = list(dict.fromkeys(blocking))
        unique_warnings = list(dict.fromkeys(warnings))

        return RunnableFeatureResult(
            runnable=not unique_blocking,
            blocking_reasons=unique_blocking,
            warnings=unique_warnings,
            parse_successful=parse_successful,
            step_validation_passed=step_validation_passed,
            yaml_validation_passed=yaml_validation_passed,
            allowed_yaml_passed=allowed_yaml_passed,
            page_frame_context_passed=page_frame_context_passed,
            step_reuse_percentage=step_reuse_result.reuse_percentage if step_reuse_result else 0.0,
            total_steps=step_reuse_result.total_steps if step_reuse_result else 0,
            matched_steps=step_reuse_result.matched_count if step_reuse_result else 0,
            unmatched_steps=step_reuse_result.unmatched_count if step_reuse_result else 0,
            yaml_keys_used=yaml_key_result.total_keys if yaml_key_result else 0,
            existing_yaml_keys=yaml_key_result.existing_count if yaml_key_result else 0,
            missing_yaml_keys=yaml_key_result.missing_count if yaml_key_result else 0,
            frame_step_count=page_frame_context_guard_result.frame_step_count
            if page_frame_context_guard_result
            else 0,
            page_step_count=page_frame_context_guard_result.page_step_count
            if page_frame_context_guard_result
            else 0,
        )

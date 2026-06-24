from __future__ import annotations

from dataclasses import dataclass
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from ptaf.ai.model.ai_generation_structured_response import AiGenerationStructuredResponse
    from ptaf.ai.validation.allowed_yaml_guard_result import AllowedYamlGuardResult
    from ptaf.ai.validation.missing_yaml_patch_suggestion import MissingYamlPatchSuggestion
    from ptaf.ai.validation.page_frame_context_guard_result import PageFrameContextGuardResult
    from ptaf.ai.validation.runnable_feature_result import RunnableFeatureResult
    from ptaf.ai.validation.step_reuse_validation_result import StepReuseValidationResult
    from ptaf.ai.validation.yaml_key_validation_result import YamlKeyValidationResult


@dataclass
class GenerationResult:
    feature_gherkin: str
    suggested_reusable_steps: list[str]
    raw_model_response: str
    reuse_trace: list
    structured_response: AiGenerationStructuredResponse
    step_reuse_validation_result: StepReuseValidationResult | None
    yaml_key_validation_result: YamlKeyValidationResult | None
    allowed_yaml_guard_result: AllowedYamlGuardResult | None
    page_frame_context_guard_result: PageFrameContextGuardResult | None
    runnable_feature_result: RunnableFeatureResult | None
    missing_yaml_patch_suggestions: list[MissingYamlPatchSuggestion]

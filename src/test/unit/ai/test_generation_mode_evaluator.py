from ptaf.ai.model.ai_generation_structured_response import AiGenerationStructuredResponse
from ptaf.ai.model.generation_result import GenerationResult
from ptaf.ai.model.ai_generation_mode import AiGenerationMode
from ptaf.ai.validation.allowed_yaml_guard_result import AllowedYamlGuardResult
from ptaf.ai.validation.generation_mode_evaluator import GenerationModeEvaluator
from ptaf.ai.validation.page_frame_context_guard_result import PageFrameContextGuardResult
from ptaf.ai.validation.runnable_feature_result import RunnableFeatureResult
from ptaf.ai.validation.step_reuse_validation_result import StepReuseValidationResult
from ptaf.ai.validation.yaml_key_validation_result import YamlKeyValidationResult


def _valid_result() -> GenerationResult:
    structured = AiGenerationStructuredResponse(
        parse_successful=True,
        feature_file="Feature: Sample\n  Scenario: One\n    Given user is on page",
    )
    step = StepReuseValidationResult(
        feature_steps=["Given user is on page"],
        matched_existing_steps=["Given user is on page"],
        unmatched_steps=[],
        claimed_reused_but_not_found=[],
        claimed_new_but_already_exists=[],
        total_steps=1,
        matched_count=1,
        unmatched_count=0,
        reuse_percentage=100.0,
        passed=True,
        warnings=[],
    )
    yaml = YamlKeyValidationResult(
        yaml_keys_used=["elements.login.username"],
        existing_keys=["elements.login.username"],
        missing_keys=[],
        suggested_patches={},
        total_keys=1,
        existing_count=1,
        missing_count=0,
        passed=True,
        warnings=[],
    )
    return GenerationResult(
        feature_gherkin=structured.feature_file,
        suggested_reusable_steps=[],
        raw_model_response="",
        reuse_trace=[],
        structured_response=structured,
        step_reuse_validation_result=step,
        yaml_key_validation_result=yaml,
        allowed_yaml_guard_result=None,
        page_frame_context_guard_result=None,
        runnable_feature_result=_runnable(True),
        missing_yaml_patch_suggestions=[],
    )


def _runnable(runnable: bool) -> RunnableFeatureResult:
    return RunnableFeatureResult(
        runnable=runnable,
        blocking_reasons=[] if runnable else ["Unknown YAML key used: elements.login.submitbutton"],
        warnings=[],
        parse_successful=runnable,
        step_validation_passed=True,
        yaml_validation_passed=True,
        allowed_yaml_passed=runnable,
        page_frame_context_passed=runnable,
        step_reuse_percentage=100.0,
        total_steps=1,
        matched_steps=1,
        unmatched_steps=0,
        yaml_keys_used=1,
        existing_yaml_keys=1,
        missing_yaml_keys=0 if runnable else 1,
        frame_step_count=0 if runnable else 1,
        page_step_count=1,
    )


def test_preview_mode_never_writes_file():
    evaluator = GenerationModeEvaluator()
    result = _valid_result()
    errors = evaluator.blocking_errors(AiGenerationMode.PREVIEW, result)
    assert not evaluator.should_write_file(AiGenerationMode.PREVIEW, errors)


def test_write_mode_writes_only_when_parse_succeeds():
    evaluator = GenerationModeEvaluator()
    result = _valid_result()
    errors = evaluator.blocking_errors(AiGenerationMode.WRITE, result)
    assert errors == []
    assert evaluator.should_write_file(AiGenerationMode.WRITE, errors)


def test_strict_mode_fails_on_missing_yaml_key():
    evaluator = GenerationModeEvaluator()
    base = _valid_result()
    yaml = YamlKeyValidationResult(
        yaml_keys_used=["elements.login.submit"],
        existing_keys=[],
        missing_keys=["elements.login.submit"],
        suggested_patches={"elements.login.submit": "login:\n  submit: \"TODO_SELECTOR\""},
        total_keys=1,
        existing_count=0,
        missing_count=1,
        passed=False,
        warnings=[],
    )
    result = GenerationResult(
        feature_gherkin=base.feature_gherkin,
        suggested_reusable_steps=base.suggested_reusable_steps,
        raw_model_response=base.raw_model_response,
        reuse_trace=base.reuse_trace,
        structured_response=base.structured_response,
        step_reuse_validation_result=base.step_reuse_validation_result,
        yaml_key_validation_result=yaml,
        allowed_yaml_guard_result=None,
        page_frame_context_guard_result=None,
        runnable_feature_result=_runnable(False),
        missing_yaml_patch_suggestions=[],
    )
    errors = evaluator.blocking_errors(AiGenerationMode.STRICT, result)
    assert any("Missing YAML key" in error for error in errors)


def test_write_mode_blocks_page_frame_guard_failure():
    evaluator = GenerationModeEvaluator()
    base = _valid_result()
    guard_result = PageFrameContextGuardResult(
        passed=False,
        invalid_frame_steps=["And we click on frame login locator loginbutton"],
        invalid_page_steps=[],
        warnings=[],
        blocking_errors=[
            "Frame step is not allowed for page 'login' locator 'loginbutton'. Use page step instead."
        ],
        frame_step_count=1,
        page_step_count=0,
    )
    result = GenerationResult(
        feature_gherkin=base.feature_gherkin,
        suggested_reusable_steps=base.suggested_reusable_steps,
        raw_model_response=base.raw_model_response,
        reuse_trace=base.reuse_trace,
        structured_response=base.structured_response,
        step_reuse_validation_result=base.step_reuse_validation_result,
        yaml_key_validation_result=base.yaml_key_validation_result,
        allowed_yaml_guard_result=None,
        page_frame_context_guard_result=guard_result,
        runnable_feature_result=RunnableFeatureResult(
            runnable=False,
            blocking_reasons=guard_result.blocking_errors,
            warnings=[],
            parse_successful=True,
            step_validation_passed=True,
            yaml_validation_passed=True,
            allowed_yaml_passed=True,
            page_frame_context_passed=False,
            step_reuse_percentage=100.0,
            total_steps=1,
            matched_steps=1,
            unmatched_steps=0,
            yaml_keys_used=1,
            existing_yaml_keys=1,
            missing_yaml_keys=0,
            frame_step_count=1,
            page_step_count=0,
        ),
        missing_yaml_patch_suggestions=[],
    )
    errors = evaluator.blocking_errors(AiGenerationMode.WRITE, result)
    assert any("Frame step is not allowed" in error for error in errors)
    assert not evaluator.should_write_file(AiGenerationMode.WRITE, errors)

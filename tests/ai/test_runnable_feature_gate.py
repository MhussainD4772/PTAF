from ptaf.ai.model.ai_generation_structured_response import AiGenerationStructuredResponse
from ptaf.ai.validation.allowed_yaml_guard_result import AllowedYamlGuardResult
from ptaf.ai.validation.page_frame_context_guard_result import PageFrameContextGuardResult
from ptaf.ai.validation.runnable_feature_gate import RunnableFeatureGate
from ptaf.ai.validation.step_reuse_validation_result import StepReuseValidationResult
from ptaf.ai.validation.yaml_key_validation_result import YamlKeyValidationResult


def _structured(parse_ok: bool, feature: str) -> AiGenerationStructuredResponse:
    return AiGenerationStructuredResponse(
        parse_successful=parse_ok,
        feature_file=feature,
        parse_errors=[] if parse_ok else ["bad"],
    )


def _step(passed: bool) -> StepReuseValidationResult:
    return StepReuseValidationResult(
        feature_steps=["Given user is on page"],
        matched_existing_steps=["Given user is on page"] if passed else [],
        unmatched_steps=[] if passed else ["Given unknown step"],
        claimed_reused_but_not_found=[],
        claimed_new_but_already_exists=[],
        total_steps=1,
        matched_count=1 if passed else 0,
        unmatched_count=0 if passed else 1,
        reuse_percentage=100.0 if passed else 0.0,
        passed=passed,
        warnings=[],
    )


def _yaml(passed: bool, total: int, existing: int, missing: int) -> YamlKeyValidationResult:
    return YamlKeyValidationResult(
        yaml_keys_used=[],
        existing_keys=[],
        missing_keys=[],
        suggested_patches={},
        total_keys=total,
        existing_count=existing,
        missing_count=missing,
        passed=passed,
        warnings=[],
    )


def _allowed(passed: bool, unknown: list[str], missing_in_feature: list[str]) -> AllowedYamlGuardResult:
    return AllowedYamlGuardResult(
        passed=passed,
        allowed_keys=[],
        unknown_keys_used=unknown,
        missing_keys_declared=[],
        missing_keys_used_in_feature=missing_in_feature,
        warnings=[],
        blocking_errors=[],
    )


def _page_frame(passed: bool, frame_count: int, page_count: int) -> PageFrameContextGuardResult:
    return PageFrameContextGuardResult(
        passed=passed,
        invalid_frame_steps=[] if passed else ["And we click on frame login locator loginbutton"],
        invalid_page_steps=[],
        warnings=[],
        blocking_errors=[] if passed else [
            "Frame step is not allowed for page 'login' locator 'loginbutton'. Use page step instead."
        ],
        frame_step_count=frame_count,
        page_step_count=page_count,
    )


def test_runnable_when_all_validations_pass():
    gate = RunnableFeatureGate()
    result = gate.evaluate(
        _structured(True, "Feature: Login\nScenario: Ok"),
        _step(True),
        _yaml(True, 1, 1, 0),
        _allowed(True, [], []),
        _page_frame(True, 0, 1),
    )
    assert result.runnable


def test_not_runnable_when_parse_failed():
    gate = RunnableFeatureGate()
    result = gate.evaluate(
        _structured(False, "Feature: Login"),
        _step(True),
        _yaml(True, 1, 1, 0),
        _allowed(True, [], []),
        _page_frame(True, 0, 0),
    )
    assert not result.runnable
    assert any("failed to parse" in reason for reason in result.blocking_reasons)


def test_not_runnable_when_allowed_yaml_guard_failed():
    gate = RunnableFeatureGate()
    result = gate.evaluate(
        _structured(True, "Feature: Login"),
        _step(True),
        _yaml(True, 1, 1, 0),
        _allowed(False, ["elements.login.submitbutton"], []),
        _page_frame(True, 0, 1),
    )
    assert not result.runnable
    assert any("Unknown YAML key used" in reason for reason in result.blocking_reasons)

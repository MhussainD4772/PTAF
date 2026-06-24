from ptaf.ai.index.step_definition_index import StepDefinitionIndex
from ptaf.ai.model.ai_generation_structured_response import AiGenerationStructuredResponse
from ptaf.ai.validation.step_reuse_validator import StepReuseValidator


def _base_response(feature_file: str) -> AiGenerationStructuredResponse:
    return AiGenerationStructuredResponse(feature_file=feature_file)


def test_matches_exact_step():
    response = _base_response(
        "Feature: Login\n  Scenario: Valid login\n    Given user is on the login page"
    )
    index = StepDefinitionIndex(["user is on the login page"])
    result = StepReuseValidator().validate(response, index)
    assert result.matched_count == 1
    assert result.unmatched_count == 0


def test_matches_string_parameterized_step():
    response = _base_response(
        'Feature: Login\n  Scenario: Enter credentials\n    When user enters "Mo" into "username"'
    )
    index = StepDefinitionIndex(["user enters {string} into {string}"])
    result = StepReuseValidator().validate(response, index)
    assert result.matched_count == 1


def test_detects_unmatched_feature_step():
    response = _base_response(
        "Feature: Transfer\n  Scenario: Transfer money\n    Given user is on transfer page"
    )
    index = StepDefinitionIndex(["user is on dashboard"])
    result = StepReuseValidator().validate(response, index)
    assert result.unmatched_count == 1
    assert len(result.unmatched_steps) == 1

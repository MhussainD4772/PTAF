from ptaf.ai.index.yaml_key_index import YamlKeyIndex
from ptaf.ai.model.ai_generation_structured_response import AiGenerationStructuredResponse
from ptaf.ai.validation.allowed_yaml_guard import AllowedYamlGuard
from ptaf.ai.validation.yaml_key_validator import YamlKeyValidator


def _response(yaml_keys_used: list[str], missing_yaml_keys: list[str]) -> AiGenerationStructuredResponse:
    return AiGenerationStructuredResponse(
        yaml_keys_used=yaml_keys_used,
        missing_yaml_keys=missing_yaml_keys,
    )


def _index_with(keys: set[str]) -> YamlKeyIndex:
    return YamlKeyIndex(keys, {})


def test_yaml_validator_matches_existing_key():
    response = _response(["elements.login.username.input"], [])
    result = YamlKeyValidator().validate(response, _index_with({"elements.login.username.input"}))
    assert result.existing_count == 1
    assert result.missing_count == 0


def test_yaml_validator_detects_missing_key():
    response = _response(["elements.login.submitbutton"], [])
    result = YamlKeyValidator().validate(
        response, _index_with({"elements.login.username.input"})
    )
    assert result.missing_count == 1
    assert "elements.login.submitbutton" in result.missing_keys


def test_allowed_yaml_guard_fails_on_unknown_key():
    response = AiGenerationStructuredResponse(
        feature_file="Feature: X\nScenario: Y",
        yaml_keys_used=["elements.login.submitbutton"],
    )
    result = AllowedYamlGuard().validate(
        response, _index_with({"elements.login.username"})
    )
    assert not result.passed
    assert "elements.login.submitbutton" in result.unknown_keys_used

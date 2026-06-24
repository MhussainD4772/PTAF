from pathlib import Path

import pytest

from ptaf.ai.config.ai_assistant_properties import AiAssistantProperties
from ptaf.ai.feature_generator_service import FeatureGeneratorService


class _MockModelClient:
    def generate(self, system_prompt: str, user_prompt: str, props: AiAssistantProperties) -> str:
        return """
<<<FEATURE_FILE>>>
Feature: Login test
  Scenario: User logs in
    Then we click on page login locator username
<<<END_FEATURE_FILE>>>
<<<REUSED_STEPS>>>
- we click on page login locator username
<<<END_REUSED_STEPS>>>
<<<NEW_STEPS_NEEDED>>>
<<<END_NEW_STEPS_NEEDED>>>
<<<YAML_KEYS_USED>>>
- elements.login.username
<<<END_YAML_KEYS_USED>>>
<<<MISSING_YAML_KEYS>>>
<<<END_MISSING_YAML_KEYS>>>
<<<WARNINGS>>>
<<<END_WARNINGS>>>
"""


def test_generate_pipeline_with_mock_client():
    project_root = Path(__file__).resolve().parents[+2]
    service = FeatureGeneratorService(AiAssistantProperties(), model_client=_MockModelClient())
    result = service.generate(project_root, "login test")
    assert result.structured_response.parse_successful
    assert result.step_reuse_validation_result is not None
    assert result.yaml_key_validation_result is not None
    assert result.runnable_feature_result is not None

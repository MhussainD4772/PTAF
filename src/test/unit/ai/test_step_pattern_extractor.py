from pathlib import Path

from ptaf.ai.index.step_definition_index import StepDefinitionIndex
from ptaf.ai.model.ai_generation_structured_response import AiGenerationStructuredResponse
from ptaf.ai.step_pattern_extractor import from_python_source
from ptaf.ai.validation.step_reuse_validator import StepReuseValidator


def test_extracts_keyword_step_parse_patterns() -> None:
    source = '''
@keyword_step(parsers.parse("we navigate to {config_key} url"))
def step_one(): ...

@keyword_step(parsers.parse('we enter value on page {element} locator {locator} value "{value}"'))
def step_two(): ...

@keyword_step("I set the request body to")
def step_three(): ...
'''
    patterns = from_python_source(source)
    assert "we navigate to {config_key} url" in patterns
    assert 'we enter value on page {element} locator {locator} value "{value}"' in patterns
    assert "I set the request body to" in patterns


def _project_root() -> Path:
    return Path(__file__).resolve().parents[4]


def test_index_finds_stepdefinitions_from_repo() -> None:
    root = _project_root()
    index = StepDefinitionIndex.build(root, ["src/test/stepdefinitions"])
    assert len(index.known_steps()) > 50
    assert any("navigate to {config_key} url" in step for step in index.known_steps())


def test_matches_google_style_feature_steps() -> None:
    feature = """Feature: Google Validation
  Scenario: Search for wooden spoon
    Given we navigate to google_url url
    And we enter value on page google_page locator search_flt value "wooden spoon"
    When we press on page google_page locator body key "Enter" keyboard
    Then we get text of elements on page google_page locator footer_text
    And we wait for some time
"""
    root = _project_root()
    index = StepDefinitionIndex.build(root, ["src/test/stepdefinitions"])
    response = AiGenerationStructuredResponse(
        feature_file=feature,
        reused_steps=[],
        new_steps_needed=[],
    )
    result = StepReuseValidator().validate(response, index)
    assert result.matched_count == 5
    assert result.unmatched_count == 0
    assert result.passed

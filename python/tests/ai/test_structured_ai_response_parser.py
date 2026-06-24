from ptaf.ai.parser.structured_ai_response_parser import parse


def test_parses_fully_valid_structured_response():
    raw = """
<<<FEATURE_FILE>>>
Feature: Login
  Scenario: Valid login
    Given I open login page
<<<END_FEATURE_FILE>>>
<<<REUSED_STEPS>>>
- I open login page
<<<END_REUSED_STEPS>>>
<<<NEW_STEPS_NEEDED>>>
- I submit MFA token
<<<END_NEW_STEPS_NEEDED>>>
<<<YAML_KEYS_USED>>>
- elements.login.username
- elements.login.password
<<<END_YAML_KEYS_USED>>>
<<<MISSING_YAML_KEYS>>>
- elements.login.submitButton
<<<END_MISSING_YAML_KEYS>>>
<<<WARNINGS>>>
- MFA selector may vary by environment
<<<END_WARNINGS>>>
"""
    parsed = parse(raw)
    assert parsed.parse_successful
    assert "Feature: Login" in parsed.feature_file
    assert len(parsed.reused_steps) == 1
    assert len(parsed.new_steps_needed) == 1
    assert len(parsed.yaml_keys_used) == 2
    assert len(parsed.missing_yaml_keys) == 1
    assert len(parsed.warnings) == 1


def test_reports_missing_section_as_parse_error():
    raw = """
<<<FEATURE_FILE>>>
Feature: Login
<<<END_FEATURE_FILE>>>
<<<REUSED_STEPS>>>
- reused step
<<<END_REUSED_STEPS>>>
<<<NEW_STEPS_NEEDED>>>
- new step
<<<END_NEW_STEPS_NEEDED>>>
<<<YAML_KEYS_USED>>>
- elements.login.username
<<<END_YAML_KEYS_USED>>>
<<<MISSING_YAML_KEYS>>>
- elements.login.submitButton
<<<END_MISSING_YAML_KEYS>>>
"""
    parsed = parse(raw)
    assert not parsed.parse_successful
    assert any("WARNINGS" in error for error in parsed.parse_errors)


def test_handles_empty_sections():
    raw = """
<<<FEATURE_FILE>>>
Feature: Empty sections
  Scenario: One
    Given something
<<<END_FEATURE_FILE>>>
<<<REUSED_STEPS>>>
<<<END_REUSED_STEPS>>>
<<<NEW_STEPS_NEEDED>>>
<<<END_NEW_STEPS_NEEDED>>>
<<<YAML_KEYS_USED>>>
<<<END_YAML_KEYS_USED>>>
<<<MISSING_YAML_KEYS>>>
<<<END_MISSING_YAML_KEYS>>>
<<<WARNINGS>>>
<<<END_WARNINGS>>>
"""
    parsed = parse(raw)
    assert parsed.parse_successful
    assert parsed.reused_steps == []
    assert parsed.new_steps_needed == []
    assert parsed.yaml_keys_used == []
    assert parsed.missing_yaml_keys == []
    assert parsed.warnings == []


def test_parses_bullet_variants():
    raw = """
<<<FEATURE_FILE>>>
Feature: Bullet parse
  Scenario: One
    Given something
<<<END_FEATURE_FILE>>>
<<<REUSED_STEPS>>>
- first
* second
<<<END_REUSED_STEPS>>>
<<<NEW_STEPS_NEEDED>>>
- third
<<<END_NEW_STEPS_NEEDED>>>
<<<YAML_KEYS_USED>>>
- elements.key
<<<END_YAML_KEYS_USED>>>
<<<MISSING_YAML_KEYS>>>
<<<END_MISSING_YAML_KEYS>>>
<<<WARNINGS>>>
<<<END_WARNINGS>>>
"""
    parsed = parse(raw)
    assert len(parsed.reused_steps) == 2
    assert parsed.reused_steps[0] == "first"
    assert parsed.reused_steps[1] == "second"

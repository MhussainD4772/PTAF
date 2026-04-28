package com.ptaf.ai.parser;

import com.ptaf.ai.model.AiGenerationStructuredResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StructuredAiResponseParserTest {

    @Test
    void parsesFullyValidStructuredResponse() {
        String raw = """
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
                """;

        AiGenerationStructuredResponse parsed = StructuredAiResponseParser.parse(raw);
        assertTrue(parsed.parseSuccessful());
        assertTrue(parsed.featureFile().contains("Feature: Login"));
        assertEquals(1, parsed.reusedSteps().size());
        assertEquals(1, parsed.newStepsNeeded().size());
        assertEquals(2, parsed.yamlKeysUsed().size());
        assertEquals(1, parsed.missingYamlKeys().size());
        assertEquals(1, parsed.warnings().size());
    }

    @Test
    void reportsMissingSectionAsParseError() {
        String raw = """
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
                """;

        AiGenerationStructuredResponse parsed = StructuredAiResponseParser.parse(raw);
        assertFalse(parsed.parseSuccessful());
        assertTrue(parsed.parseErrors().stream().anyMatch(e -> e.contains("WARNINGS")));
    }

    @Test
    void handlesEmptySections() {
        String raw = """
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
                """;

        AiGenerationStructuredResponse parsed = StructuredAiResponseParser.parse(raw);
        assertTrue(parsed.parseSuccessful());
        assertTrue(parsed.reusedSteps().isEmpty());
        assertTrue(parsed.newStepsNeeded().isEmpty());
        assertTrue(parsed.yamlKeysUsed().isEmpty());
        assertTrue(parsed.missingYamlKeys().isEmpty());
        assertTrue(parsed.warnings().isEmpty());
    }

    @Test
    void parsesBulletVariants() {
        String raw = """
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
                """;

        AiGenerationStructuredResponse parsed = StructuredAiResponseParser.parse(raw);
        assertEquals(2, parsed.reusedSteps().size());
        assertEquals("first", parsed.reusedSteps().get(0));
        assertEquals("second", parsed.reusedSteps().get(1));
    }

    @Test
    void extractsFeatureFileSectionAsIs() {
        String raw = """
                <<<FEATURE_FILE>>>
                Feature: Transfer funds
                  Scenario: Transfer successfully
                    Given user has balance
                    When user transfers funds
                    Then transfer should succeed
                <<<END_FEATURE_FILE>>>
                <<<REUSED_STEPS>>>
                - user has balance
                <<<END_REUSED_STEPS>>>
                <<<NEW_STEPS_NEEDED>>>
                - user transfers funds
                <<<END_NEW_STEPS_NEEDED>>>
                <<<YAML_KEYS_USED>>>
                - elements.transfer.amount
                <<<END_YAML_KEYS_USED>>>
                <<<MISSING_YAML_KEYS>>>
                <<<END_MISSING_YAML_KEYS>>>
                <<<WARNINGS>>>
                <<<END_WARNINGS>>>
                """;

        AiGenerationStructuredResponse parsed = StructuredAiResponseParser.parse(raw);
        assertTrue(parsed.featureFile().startsWith("Feature: Transfer funds"));
        assertTrue(parsed.featureFile().contains("Then transfer should succeed"));
    }
}

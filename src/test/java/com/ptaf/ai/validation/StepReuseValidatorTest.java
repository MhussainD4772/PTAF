package com.ptaf.ai.validation;

import com.ptaf.ai.index.StepDefinitionIndex;
import com.ptaf.ai.model.AiGenerationStructuredResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StepReuseValidatorTest {

    private final StepReuseValidator validator = new StepReuseValidator();

    @Test
    void matchesExactStep() {
        AiGenerationStructuredResponse response = baseResponse("""
                Feature: Login
                  Scenario: Valid login
                    Given user is on the login page
                """);
        StepDefinitionIndex index = new StepDefinitionIndex(List.of("user is on the login page"));

        StepReuseValidationResult result = validator.validate(response, index);
        assertEquals(1, result.matchedCount());
        assertEquals(0, result.unmatchedCount());
    }

    @Test
    void matchesStringParameterizedStep() {
        AiGenerationStructuredResponse response = baseResponse("""
                Feature: Login
                  Scenario: Enter credentials
                    When user enters "Mo" into "username"
                """);
        StepDefinitionIndex index = new StepDefinitionIndex(List.of("user enters {string} into {string}"));

        StepReuseValidationResult result = validator.validate(response, index);
        assertEquals(1, result.matchedCount());
    }

    @Test
    void matchesIntParameterizedStep() {
        AiGenerationStructuredResponse response = baseResponse("""
                Feature: OTP
                  Scenario: Enter code
                    Then user should see 2 attempts left
                """);
        StepDefinitionIndex index = new StepDefinitionIndex(List.of("user should see {int} attempts left"));

        StepReuseValidationResult result = validator.validate(response, index);
        assertEquals(1, result.matchedCount());
    }

    @Test
    void detectsUnmatchedFeatureStep() {
        AiGenerationStructuredResponse response = baseResponse("""
                Feature: Transfer
                  Scenario: Transfer money
                    Given user is on transfer page
                """);
        StepDefinitionIndex index = new StepDefinitionIndex(List.of("user is on dashboard"));

        StepReuseValidationResult result = validator.validate(response, index);
        assertEquals(1, result.unmatchedCount());
        assertEquals(1, result.unmatchedSteps().size());
    }

    @Test
    void detectsClaimedReusedStepNotFound() {
        AiGenerationStructuredResponse response = baseResponse("""
                Feature: Transfer
                  Scenario: Transfer money
                    Given user is on transfer page
                """);
        response.setReusedSteps(List.of("user clicks confirm transfer"));
        StepDefinitionIndex index = new StepDefinitionIndex(List.of("user is on transfer page"));

        StepReuseValidationResult result = validator.validate(response, index);
        assertEquals(1, result.claimedReusedButNotFound().size());
        assertTrue(result.warnings().stream().anyMatch(w -> w.contains("claimed reused step not found")));
    }

    @Test
    void detectsClaimedNewStepAlreadyExists() {
        AiGenerationStructuredResponse response = baseResponse("""
                Feature: Login
                  Scenario: Valid login
                    Given user is on the login page
                """);
        response.setNewStepsNeeded(List.of("user is on the login page"));
        StepDefinitionIndex index = new StepDefinitionIndex(List.of("user is on the login page"));

        StepReuseValidationResult result = validator.validate(response, index);
        assertEquals(1, result.claimedNewButAlreadyExists().size());
        assertTrue(result.warnings().stream().anyMatch(w -> w.contains("claimed new step already exists")));
    }

    @Test
    void calculatesReusePercentage() {
        AiGenerationStructuredResponse response = baseResponse("""
                Feature: Mixed
                  Scenario: Mixed steps
                    Given user is on dashboard
                    When user clicks submit
                    Then user sees success
                    And user logs out
                """);
        response.setNewStepsNeeded(List.of("user logs out"));
        StepDefinitionIndex index = new StepDefinitionIndex(List.of(
                "user is on dashboard",
                "user clicks submit",
                "user sees success"
        ));

        StepReuseValidationResult result = validator.validate(response, index);
        assertEquals(4, result.totalSteps());
        assertEquals(3, result.matchedCount());
        assertEquals(1, result.unmatchedCount());
        assertEquals(75.0, result.reusePercentage());
        assertTrue(result.passed());
    }

    @Test
    void failsWhenUnmatchedStepNotListedAsNew() {
        AiGenerationStructuredResponse response = baseResponse("""
                Feature: Mixed
                  Scenario: Mixed steps
                    Given user is on dashboard
                    Then user logs out
                """);
        StepDefinitionIndex index = new StepDefinitionIndex(List.of("user is on dashboard"));

        StepReuseValidationResult result = validator.validate(response, index);
        assertFalse(result.passed());
    }

    private static AiGenerationStructuredResponse baseResponse(String featureFile) {
        AiGenerationStructuredResponse response = new AiGenerationStructuredResponse();
        response.setFeatureFile(featureFile);
        response.setReusedSteps(List.of());
        response.setNewStepsNeeded(List.of());
        response.setYamlKeysUsed(List.of());
        response.setMissingYamlKeys(List.of());
        response.setWarnings(List.of());
        response.setParseSuccessful(true);
        response.setParseErrors(List.of());
        return response;
    }
}

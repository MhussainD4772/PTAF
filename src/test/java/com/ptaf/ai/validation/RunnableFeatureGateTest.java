package com.ptaf.ai.validation;

import com.ptaf.ai.model.AiGenerationStructuredResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RunnableFeatureGateTest {
    private final RunnableFeatureGate gate = new RunnableFeatureGate();

    @Test
    void runnableWhenAllValidationsPass() {
        RunnableFeatureResult result = gate.evaluate(
                structured(true, "Feature: Login\nScenario: Ok"),
                step(true),
                yaml(true, 1, 1, 0),
                allowed(true, List.of(), List.of()),
                pageFrame(true, 0, 1)
        );
        assertTrue(result.runnable());
    }

    @Test
    void notRunnableWhenParseFailed() {
        RunnableFeatureResult result = gate.evaluate(
                structured(false, "Feature: Login"),
                step(true),
                yaml(true, 1, 1, 0),
                allowed(true, List.of(), List.of()),
                pageFrame(true, 0, 0)
        );
        assertFalse(result.runnable());
        assertTrue(result.blockingReasons().stream().anyMatch(r -> r.contains("failed to parse")));
    }

    @Test
    void notRunnableWhenFeatureEmpty() {
        RunnableFeatureResult result = gate.evaluate(
                structured(true, ""),
                step(true),
                yaml(true, 1, 1, 0),
                allowed(true, List.of(), List.of()),
                pageFrame(true, 0, 0)
        );
        assertFalse(result.runnable());
        assertTrue(result.blockingReasons().stream().anyMatch(r -> r.contains("empty")));
    }

    @Test
    void notRunnableWhenStepValidationFailed() {
        RunnableFeatureResult result = gate.evaluate(
                structured(true, "Feature: Login"),
                step(false),
                yaml(true, 1, 1, 0),
                allowed(true, List.of(), List.of()),
                pageFrame(true, 0, 1)
        );
        assertFalse(result.runnable());
        assertTrue(result.blockingReasons().stream().anyMatch(r -> r.contains("Step validation failed")));
    }

    @Test
    void notRunnableWhenYamlValidationFailed() {
        RunnableFeatureResult result = gate.evaluate(
                structured(true, "Feature: Login"),
                step(true),
                yaml(false, 2, 1, 1),
                allowed(true, List.of(), List.of()),
                pageFrame(true, 0, 1)
        );
        assertFalse(result.runnable());
        assertTrue(result.blockingReasons().stream().anyMatch(r -> r.contains("YAML validation failed")));
    }

    @Test
    void notRunnableWhenAllowedYamlGuardFailed() {
        RunnableFeatureResult result = gate.evaluate(
                structured(true, "Feature: Login"),
                step(true),
                yaml(true, 1, 1, 0),
                allowed(false, List.of("elements.login.submitbutton"), List.of()),
                pageFrame(true, 0, 1)
        );
        assertFalse(result.runnable());
        assertTrue(result.blockingReasons().stream().anyMatch(r -> r.contains("Unknown YAML key used")));
    }

    @Test
    void notRunnableWhenMissingYamlUsedInFeature() {
        RunnableFeatureResult result = gate.evaluate(
                structured(true, "Feature: Login\nThen elements.login.submitbutton"),
                step(true),
                yaml(false, 1, 0, 1),
                allowed(false, List.of(), List.of("elements.login.submitbutton")),
                pageFrame(true, 0, 1)
        );
        assertFalse(result.runnable());
        assertTrue(result.blockingReasons().stream().anyMatch(r -> r.contains("Missing YAML key appears inside feature file")));
    }

    @Test
    void blockingReasonsAreClear() {
        RunnableFeatureResult result = gate.evaluate(
                structured(false, ""),
                step(false),
                yaml(false, 1, 0, 1),
                allowed(false, List.of("elements.login.x"), List.of("elements.login.x")),
                pageFrame(false, 1, 0)
        );
        assertFalse(result.runnable());
        assertTrue(result.blockingReasons().size() >= 3);
    }

    @Test
    void notRunnableWhenPageFrameGuardFails() {
        RunnableFeatureResult result = gate.evaluate(
                structured(true, "Feature: Login"),
                step(true),
                yaml(true, 1, 1, 0),
                allowed(true, List.of(), List.of()),
                pageFrame(false, 2, 0)
        );
        assertFalse(result.runnable());
        assertTrue(result.blockingReasons().stream().anyMatch(r -> r.contains("Page/frame context guard failed")));
    }

    private static AiGenerationStructuredResponse structured(boolean parseOk, String feature) {
        AiGenerationStructuredResponse response = new AiGenerationStructuredResponse();
        response.setParseSuccessful(parseOk);
        response.setFeatureFile(feature);
        response.setParseErrors(parseOk ? List.of() : List.of("bad"));
        response.setReusedSteps(List.of());
        response.setNewStepsNeeded(List.of());
        response.setYamlKeysUsed(List.of());
        response.setMissingYamlKeys(List.of());
        response.setWarnings(List.of());
        return response;
    }

    private static StepReuseValidationResult step(boolean passed) {
        return new StepReuseValidationResult(
                List.of("Given user is on page"),
                passed ? List.of("Given user is on page") : List.of(),
                passed ? List.of() : List.of("Given unknown step"),
                List.of(),
                List.of(),
                1,
                passed ? 1 : 0,
                passed ? 0 : 1,
                passed ? 100.0 : 0.0,
                passed,
                List.of()
        );
    }

    private static YamlKeyValidationResult yaml(boolean passed, int total, int existing, int missing) {
        return new YamlKeyValidationResult(
                List.of(),
                List.of(),
                List.of(),
                Map.of(),
                total,
                existing,
                missing,
                passed,
                List.of()
        );
    }

    private static AllowedYamlGuardResult allowed(boolean passed, List<String> unknown, List<String> missingInFeature) {
        return new AllowedYamlGuardResult(
                passed,
                List.of(),
                unknown,
                List.of(),
                missingInFeature,
                List.of(),
                List.of()
        );
    }

    private static PageFrameContextGuardResult pageFrame(boolean passed, int frameCount, int pageCount) {
        return new PageFrameContextGuardResult(
                passed,
                passed ? List.of() : List.of("And we click on frame login locator loginbutton"),
                List.of(),
                List.of(),
                passed ? List.of() : List.of("Frame step is not allowed for page 'login' locator 'loginbutton'. Use page step instead."),
                frameCount,
                pageCount
        );
    }
}

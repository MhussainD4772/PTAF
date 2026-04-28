package com.ptaf.ai.validation;

import com.ptaf.ai.model.AiGenerationMode;
import com.ptaf.ai.model.AiGenerationStructuredResponse;
import com.ptaf.ai.model.GenerationResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenerationModeEvaluatorTest {
    private final GenerationModeEvaluator evaluator = new GenerationModeEvaluator();

    @Test
    void previewModeNeverWritesFile() {
        GenerationResult result = validResult();
        var errors = evaluator.blockingErrors(AiGenerationMode.PREVIEW, result);
        assertFalse(evaluator.shouldWriteFile(AiGenerationMode.PREVIEW, errors));
    }

    @Test
    void writeModeWritesOnlyWhenParseSucceeds() {
        GenerationResult result = validResult();
        var errors = evaluator.blockingErrors(AiGenerationMode.WRITE, result);
        assertTrue(errors.isEmpty());
        assertTrue(evaluator.shouldWriteFile(AiGenerationMode.WRITE, errors));
    }

    @Test
    void writeModeBlocksInvalidParse() {
        GenerationResult result = invalidParseResult();
        var errors = evaluator.blockingErrors(AiGenerationMode.WRITE, result);
        assertFalse(errors.isEmpty());
        assertFalse(evaluator.shouldWriteFile(AiGenerationMode.WRITE, errors));
    }

    @Test
    void strictModeFailsOnMissingYamlKey() {
        GenerationResult result = resultWithYamlMissing();
        var errors = evaluator.blockingErrors(AiGenerationMode.STRICT, result);
        assertTrue(errors.stream().anyMatch(e -> e.contains("Missing YAML key")));
    }

    @Test
    void strictModeFailsOnClaimedReusedNotFound() {
        GenerationResult result = resultWithClaimedReusedNotFound();
        var errors = evaluator.blockingErrors(AiGenerationMode.STRICT, result);
        assertTrue(errors.stream().anyMatch(e -> e.contains("Reused step claimed but not found")));
    }

    @Test
    void strictModePassesWhenValid() {
        GenerationResult result = validResult();
        var errors = evaluator.blockingErrors(AiGenerationMode.STRICT, result);
        assertTrue(errors.isEmpty());
    }

    @Test
    void writeModeBlocksNonRunnableFeature() {
        GenerationResult result = resultWithNonRunnableGate();
        var errors = evaluator.blockingErrors(AiGenerationMode.WRITE, result);
        assertFalse(errors.isEmpty());
        assertFalse(evaluator.shouldWriteFile(AiGenerationMode.WRITE, errors));
    }

    @Test
    void strictModeFailsNonRunnableFeature() {
        GenerationResult result = resultWithNonRunnableGate();
        var errors = evaluator.blockingErrors(AiGenerationMode.STRICT, result);
        assertTrue(errors.stream().anyMatch(e -> e.contains("Unknown YAML key used")));
    }

    @Test
    void writeModeBlocksWhenPageFrameGuardFails() {
        GenerationResult result = resultWithPageFrameGuardFailure();
        var errors = evaluator.blockingErrors(AiGenerationMode.WRITE, result);
        assertTrue(errors.stream().anyMatch(e -> e.contains("Frame step is not allowed")));
        assertFalse(evaluator.shouldWriteFile(AiGenerationMode.WRITE, errors));
    }

    @Test
    void strictModeFailsWhenPageFrameGuardFails() {
        GenerationResult result = resultWithPageFrameGuardFailure();
        var errors = evaluator.blockingErrors(AiGenerationMode.STRICT, result);
        assertTrue(errors.stream().anyMatch(e -> e.contains("Frame step is not allowed")));
    }

    private static GenerationResult validResult() {
        AiGenerationStructuredResponse structured = new AiGenerationStructuredResponse();
        structured.setParseSuccessful(true);
        structured.setFeatureFile("""
                Feature: Sample
                  Scenario: One
                    Given user is on page
                """);
        StepReuseValidationResult step = new StepReuseValidationResult(
                List.of("Given user is on page"),
                List.of("Given user is on page"),
                List.of(),
                List.of(),
                List.of(),
                1, 1, 0, 100.0, true, List.of()
        );
        YamlKeyValidationResult yaml = new YamlKeyValidationResult(
                List.of("elements.login.username"),
                List.of("elements.login.username"),
                List.of(),
                Map.of(),
                1, 1, 0, true, List.of()
        );
        return new GenerationResult(
                structured.featureFile(),
                List.of(),
                "",
                List.of(),
                structured,
                step,
                yaml,
                null,
                null,
                runnable(true),
                List.of()
        );
    }

    private static GenerationResult invalidParseResult() {
        AiGenerationStructuredResponse structured = new AiGenerationStructuredResponse();
        structured.setParseSuccessful(false);
        structured.setParseErrors(List.of("Missing required section: FEATURE_FILE"));
        return new GenerationResult(
                "",
                List.of(),
                "",
                List.of(),
                structured,
                null,
                null,
                null,
                null,
                null,
                List.of()
        );
    }

    private static GenerationResult resultWithYamlMissing() {
        GenerationResult base = validResult();
        YamlKeyValidationResult yaml = new YamlKeyValidationResult(
                List.of("elements.login.submit"),
                List.of(),
                List.of("elements.login.submit"),
                Map.of("elements.login.submit", "login:\n  submit: \"TODO_SELECTOR\""),
                1, 0, 1, false, List.of()
        );
        return new GenerationResult(
                base.featureGherkin(),
                base.suggestedReusableSteps(),
                base.rawModelResponse(),
                base.reuseTrace(),
                base.structuredResponse(),
                base.stepReuseValidationResult(),
                yaml,
                null,
                null,
                runnable(false),
                List.of()
        );
    }

    private static GenerationResult resultWithClaimedReusedNotFound() {
        GenerationResult base = validResult();
        StepReuseValidationResult step = new StepReuseValidationResult(
                List.of("Given user is on page"),
                List.of("Given user is on page"),
                List.of(),
                List.of("user clicks login"),
                List.of(),
                1, 1, 0, 100.0, true, List.of("AI claimed reused step not found: user clicks login")
        );
        return new GenerationResult(
                base.featureGherkin(),
                base.suggestedReusableSteps(),
                base.rawModelResponse(),
                base.reuseTrace(),
                base.structuredResponse(),
                step,
                base.yamlKeyValidationResult(),
                null,
                null,
                runnable(false),
                List.of()
        );
    }

    private static GenerationResult resultWithNonRunnableGate() {
        GenerationResult base = validResult();
        return new GenerationResult(
                base.featureGherkin(),
                base.suggestedReusableSteps(),
                base.rawModelResponse(),
                base.reuseTrace(),
                base.structuredResponse(),
                base.stepReuseValidationResult(),
                base.yamlKeyValidationResult(),
                new AllowedYamlGuardResult(
                        false,
                        List.of(),
                        List.of("elements.login.submitbutton"),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of("Unknown YAML key used: elements.login.submitbutton")
                ),
                null,
                runnable(false),
                List.of()
        );
    }

    private static GenerationResult resultWithPageFrameGuardFailure() {
        GenerationResult base = validResult();
        PageFrameContextGuardResult guardResult = new PageFrameContextGuardResult(
                false,
                List.of("And we click on frame login locator loginbutton"),
                List.of(),
                List.of(),
                List.of("Frame step is not allowed for page 'login' locator 'loginbutton'. Use page step instead."),
                1,
                0
        );
        return new GenerationResult(
                base.featureGherkin(),
                base.suggestedReusableSteps(),
                base.rawModelResponse(),
                base.reuseTrace(),
                base.structuredResponse(),
                base.stepReuseValidationResult(),
                base.yamlKeyValidationResult(),
                null,
                guardResult,
                new RunnableFeatureResult(
                        false,
                        List.of("Frame step is not allowed for page 'login' locator 'loginbutton'. Use page step instead."),
                        List.of(),
                        true,
                        true,
                        true,
                        true,
                        false,
                        100.0,
                        1,
                        1,
                        0,
                        1,
                        1,
                        0,
                        1,
                        0
                ),
                List.of()
        );
    }

    private static RunnableFeatureResult runnable(boolean runnable) {
        return new RunnableFeatureResult(
                runnable,
                runnable ? List.of() : List.of("Unknown YAML key used: elements.login.submitbutton"),
                List.of(),
                runnable,
                true,
                true,
                runnable,
                runnable,
                100.0,
                1,
                1,
                0,
                1,
                1,
                runnable ? 0 : 1,
                runnable ? 0 : 1,
                1
        );
    }
}

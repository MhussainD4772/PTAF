package com.ptaf.ai;

import com.ptaf.ai.audit.AiGenerationAuditLogger;
import com.ptaf.ai.audit.AiGenerationAuditRecord;
import com.ptaf.ai.config.AiAssistantProperties;
import com.ptaf.ai.model.AiGenerationExecutionResult;
import com.ptaf.ai.model.AiGenerationMode;
import com.ptaf.ai.model.GenerationResult;
import com.ptaf.ai.policy.AiPolicy;
import com.ptaf.ai.validation.GenerationModeEvaluator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenerationFlowIntegrationTest {

    @TempDir
    Path tempRoot;

    @Test
    void runsFullGenerateFlowAcrossPreviewWriteStrictWithMockedModel() throws Exception {
        prepareProjectLayout(tempRoot);

        AiModelClient mockedModel = (system, user, props) -> """
                <<<FEATURE_FILE>>>
                Feature: Login flow
                  Scenario: Successful login
                    Given user is on the login page
                <<<END_FEATURE_FILE>>>
                <<<REUSED_STEPS>>>
                - user is on the login page
                <<<END_REUSED_STEPS>>>
                <<<NEW_STEPS_NEEDED>>>
                <<<END_NEW_STEPS_NEEDED>>>
                <<<YAML_KEYS_USED>>>
                - elements.login.username.input
                <<<END_YAML_KEYS_USED>>>
                <<<MISSING_YAML_KEYS>>>
                <<<END_MISSING_YAML_KEYS>>>
                <<<WARNINGS>>>
                <<<END_WARNINGS>>>
                """;

        FeatureGeneratorService service = new FeatureGeneratorService(new AiAssistantProperties(), new AiPolicy(), mockedModel);
        GenerationResult generated = service.generate(tempRoot, "User logs in with username");
        GenerationModeEvaluator evaluator = new GenerationModeEvaluator();

        var previewErrors = evaluator.blockingErrors(AiGenerationMode.PREVIEW, generated);
        AiGenerationExecutionResult preview = new AiGenerationExecutionResult(
                AiGenerationMode.PREVIEW, previewErrors.isEmpty(), false, null, generated.structuredResponse(),
                generated.stepReuseValidationResult(), generated.yamlKeyValidationResult(), previewErrors, List.of()
        );
        assertTrue(preview.success());
        assertTrue(preview.blockingErrors().isEmpty());

        var writeErrors = evaluator.blockingErrors(AiGenerationMode.WRITE, generated);
        Path output = tempRoot.resolve("target/ai-proposals/e2e-generated.feature");
        if (evaluator.shouldWriteFile(AiGenerationMode.WRITE, writeErrors)) {
            service.writeFeatureFile(output, generated, false);
        }
        assertTrue(Files.exists(output));

        var strictErrors = evaluator.blockingErrors(AiGenerationMode.STRICT, generated);
        AiGenerationExecutionResult strict = new AiGenerationExecutionResult(
                AiGenerationMode.STRICT, strictErrors.isEmpty(), false, output.toString(), generated.structuredResponse(),
                generated.stepReuseValidationResult(), generated.yamlKeyValidationResult(), strictErrors, List.of()
        );
        assertTrue(strict.success());

        AiGenerationAuditLogger.WriteResult auditWrite = new AiGenerationAuditLogger().append(
                tempRoot,
                true,
                "target/ai-audit/generation-audit.jsonl",
                new AiGenerationAuditRecord(
                        UUID.randomUUID().toString(),
                        Instant.now().toString(),
                        "generate",
                        "STRICT",
                        "gemini-2.5-flash",
                        "phase1-v1",
                        AiGenerationAuditLogger.sha256("User logs in with username"),
                        output.toString(),
                        generated.structuredResponse().parseSuccessful(),
                        generated.stepReuseValidationResult().passed(),
                        generated.yamlKeyValidationResult().passed(),
                        true,
                        generated.structuredResponse().reusedSteps().size(),
                        generated.structuredResponse().newStepsNeeded().size(),
                        generated.yamlKeyValidationResult().missingCount(),
                        List.of(),
                        strictErrors
                )
        );
        assertTrue(auditWrite.written());
        assertEquals(2, Files.readAllLines(tempRoot.resolve("target/ai-audit/generation-audit.jsonl"), StandardCharsets.UTF_8).size());
    }

    private static void prepareProjectLayout(Path root) throws Exception {
        Path stepDir = root.resolve("src/test/java/com/ptaf/stepdefinitions");
        Path elementsDir = root.resolve("src/test/resources/elements");
        Path featuresDir = root.resolve("src/test/resources/features");
        Files.createDirectories(stepDir);
        Files.createDirectories(elementsDir);
        Files.createDirectories(featuresDir);

        Files.writeString(stepDir.resolve("LoginSteps.java"), """
                package com.ptaf.stepdefinitions;
                import io.cucumber.java.en.Given;
                public class LoginSteps {
                    @Given("user is on the login page")
                    public void userOnLoginPage() {}
                }
                """, StandardCharsets.UTF_8);

        Files.writeString(elementsDir.resolve("login.yml"), """
                login:
                  username:
                    input: "#username"
                """, StandardCharsets.UTF_8);
    }
}

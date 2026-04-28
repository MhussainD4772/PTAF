package com.ptaf.ai;

import com.ptaf.ai.model.AiGenerationStructuredResponse;
import com.ptaf.ai.model.GenerationResult;
import com.ptaf.ai.validation.StepReuseValidationResult;
import com.ptaf.ai.validation.YamlKeyValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureGeneratorServiceWriteTest {

    @TempDir
    Path tempDir;

    @Test
    void writeModeDoesNotOverwriteExistingFileSilently() throws Exception {
        FeatureGeneratorService service = new FeatureGeneratorService(new com.ptaf.ai.config.AiAssistantProperties());
        Path output = tempDir.resolve("generated.feature");
        Files.writeString(output, "Feature: Old\n", StandardCharsets.UTF_8);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.writeFeatureFile(output, sampleResult(), false)
        );
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    void writeModeCanOverwriteWhenExplicitlyEnabled() throws Exception {
        FeatureGeneratorService service = new FeatureGeneratorService(new com.ptaf.ai.config.AiAssistantProperties());
        Path output = tempDir.resolve("generated.feature");
        Files.writeString(output, "Feature: Old\n", StandardCharsets.UTF_8);

        service.writeFeatureFile(output, sampleResult(), true);
        String content = Files.readString(output, StandardCharsets.UTF_8);
        assertTrue(content.startsWith("Feature: New"));
        assertEquals("Feature: New\n", content);
    }

    private static GenerationResult sampleResult() {
        AiGenerationStructuredResponse structured = new AiGenerationStructuredResponse();
        structured.setParseSuccessful(true);
        structured.setFeatureFile("Feature: New");

        StepReuseValidationResult step = new StepReuseValidationResult(
                List.of(), List.of(), List.of(), List.of(), List.of(),
                0, 0, 0, 0.0, true, List.of()
        );
        YamlKeyValidationResult yaml = new YamlKeyValidationResult(
                List.of(), List.of(), List.of(), Map.of(), 0, 0, 0, true, List.of()
        );
        return new GenerationResult("Feature: New", List.of(), "", List.of(), structured, step, yaml, null, null, List.of());
    }
}

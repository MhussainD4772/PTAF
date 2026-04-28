package com.ptaf.ai.model;

import com.ptaf.ai.validation.StepReuseValidationResult;
import com.ptaf.ai.validation.YamlKeyValidationResult;

import java.util.List;

public record AiGenerationExecutionResult(
        AiGenerationMode mode,
        boolean success,
        boolean fileWritten,
        String outputPath,
        AiGenerationStructuredResponse structuredResponse,
        StepReuseValidationResult stepReuseValidationResult,
        YamlKeyValidationResult yamlKeyValidationResult,
        List<String> blockingErrors,
        List<String> warnings
) {
}

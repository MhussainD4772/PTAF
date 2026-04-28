package com.ptaf.ai.audit;

import java.util.List;

public record AiGenerationAuditRecord(
        String requestId,
        String timestamp,
        String command,
        String mode,
        String model,
        String promptVersion,
        String requirementHash,
        String outputPath,
        boolean parseSuccessful,
        boolean stepValidationPassed,
        boolean yamlValidationPassed,
        boolean fileWritten,
        int reusedStepsCount,
        int newStepsCount,
        int missingYamlKeysCount,
        List<String> warnings,
        List<String> blockingErrors
) {
}

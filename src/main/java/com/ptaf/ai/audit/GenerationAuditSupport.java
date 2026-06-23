package com.ptaf.ai.audit;

import com.ptaf.ai.config.AiAssistantProperties;
import com.ptaf.ai.model.AiGenerationMode;
import com.ptaf.ai.model.GenerationResult;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Builds and appends generation audit records for CLI and HTTP entry points. */
public final class GenerationAuditSupport {

    private GenerationAuditSupport() {
    }

    public static List<String> collectWarnings(GenerationResult result) {
        if (result == null) {
            return List.of();
        }
        List<String> warnings = new ArrayList<>();
        warnings.addAll(result.structuredResponse().warnings());
        if (result.stepReuseValidationResult() != null) {
            warnings.addAll(result.stepReuseValidationResult().warnings());
        }
        if (result.yamlKeyValidationResult() != null) {
            warnings.addAll(result.yamlKeyValidationResult().warnings());
        }
        if (result.allowedYamlGuardResult() != null) {
            warnings.addAll(result.allowedYamlGuardResult().warnings());
        }
        if (result.pageFrameContextGuardResult() != null) {
            warnings.addAll(result.pageFrameContextGuardResult().warnings());
        }
        if (result.runnableFeatureResult() != null) {
            warnings.addAll(result.runnableFeatureResult().warnings());
        }
        if (result.missingYamlPatchSuggestions() != null) {
            for (var suggestion : result.missingYamlPatchSuggestions()) {
                warnings.addAll(suggestion.warnings());
            }
        }
        return warnings;
    }

    public static AiGenerationAuditRecord buildRecord(
            AiGenerationMode mode,
            AiAssistantProperties props,
            String requirement,
            Path requestedOutput,
            Path writtenOutput,
            GenerationResult result,
            List<String> blockingErrors,
            List<String> warnings
    ) {
        boolean parseOk = result != null && result.structuredResponse() != null && result.structuredResponse().parseSuccessful();
        boolean stepOk = result != null
                && result.stepReuseValidationResult() != null
                && result.stepReuseValidationResult().passed();
        boolean yamlOk = result != null
                && result.yamlKeyValidationResult() != null
                && result.yamlKeyValidationResult().passed();
        int reused = result != null && result.structuredResponse() != null ? result.structuredResponse().reusedSteps().size() : 0;
        int newSteps = result != null && result.structuredResponse() != null ? result.structuredResponse().newStepsNeeded().size() : 0;
        int missingYaml = result != null && result.yamlKeyValidationResult() != null ? result.yamlKeyValidationResult().missingCount() : 0;
        String outputPath = writtenOutput != null ? writtenOutput.toString() : requestedOutput.toString();

        return new AiGenerationAuditRecord(
                UUID.randomUUID().toString(),
                Instant.now().toString(),
                "generate",
                mode.name(),
                props.model(),
                props.promptVersion(),
                AiGenerationAuditLogger.sha256(requirement),
                outputPath,
                parseOk,
                stepOk,
                yamlOk,
                writtenOutput != null,
                reused,
                newSteps,
                missingYaml,
                warnings,
                blockingErrors
        );
    }

    public static AiGenerationAuditLogger.WriteResult append(
            Path projectRoot,
            AiAssistantProperties props,
            AiGenerationMode mode,
            String requirement,
            Path requestedOutput,
            Path writtenOutput,
            GenerationResult result,
            List<String> blockingErrors
    ) {
        List<String> warnings = collectWarnings(result);
        AiGenerationAuditRecord record = buildRecord(
                mode,
                props,
                requirement,
                requestedOutput,
                writtenOutput,
                result,
                blockingErrors,
                warnings
        );
        return new AiGenerationAuditLogger().append(
                projectRoot,
                props.auditEnabled(),
                props.auditOutputPath(),
                record
        );
    }
}

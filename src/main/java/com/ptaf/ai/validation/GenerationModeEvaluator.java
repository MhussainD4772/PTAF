package com.ptaf.ai.validation;

import com.ptaf.ai.model.AiGenerationMode;
import com.ptaf.ai.model.AiGenerationStructuredResponse;
import com.ptaf.ai.model.GenerationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Centralized mode safety rules for generation execution.
 */
public final class GenerationModeEvaluator {

    public List<String> blockingErrors(AiGenerationMode mode, GenerationResult result) {
        List<String> errors = new ArrayList<>();
        AiGenerationStructuredResponse structured = result.structuredResponse();
        StepReuseValidationResult step = result.stepReuseValidationResult();
        YamlKeyValidationResult yaml = result.yamlKeyValidationResult();

        if (!structured.parseSuccessful()) {
            errors.add("Structured parse failed");
            for (String parseError : structured.parseErrors()) {
                errors.add("Parse error: " + parseError);
            }
        }
        if (result.featureGherkin() == null || result.featureGherkin().isBlank()) {
            errors.add("Generated feature is empty");
        }

        if (mode == AiGenerationMode.STRICT) {
            if (yaml != null && !yaml.missingKeys().isEmpty()) {
                for (String key : yaml.missingKeys()) {
                    errors.add("Missing YAML key: " + key);
                }
            }
            if (step != null && !step.claimedReusedButNotFound().isEmpty()) {
                for (String s : step.claimedReusedButNotFound()) {
                    errors.add("Reused step claimed but not found: " + s);
                }
            }
            if (step != null && !step.passed()) {
                errors.add("One or more feature steps are unmatched and not listed under NEW_STEPS_NEEDED");
            }
        }

        return errors;
    }

    public boolean shouldWriteFile(AiGenerationMode mode, List<String> blockingErrors) {
        return mode == AiGenerationMode.WRITE && blockingErrors.isEmpty();
    }
}

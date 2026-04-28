package com.ptaf.ai.validation;

import com.ptaf.ai.model.AiGenerationStructuredResponse;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public final class RunnableFeatureGate {

    public RunnableFeatureResult evaluate(
            AiGenerationStructuredResponse structuredResponse,
            StepReuseValidationResult stepReuseResult,
            YamlKeyValidationResult yamlKeyResult,
            AllowedYamlGuardResult allowedYamlGuardResult,
            PageFrameContextGuardResult pageFrameContextGuardResult
    ) {
        List<String> blocking = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        boolean parseSuccessful = structuredResponse != null && structuredResponse.parseSuccessful();
        boolean featureEmpty = structuredResponse == null
                || structuredResponse.featureFile() == null
                || structuredResponse.featureFile().isBlank();
        boolean stepValidationPassed = stepReuseResult != null && stepReuseResult.passed();
        boolean yamlValidationPassed = yamlKeyResult != null && yamlKeyResult.passed();
        boolean allowedYamlPassed = allowedYamlGuardResult != null && allowedYamlGuardResult.passed();
        boolean pageFrameContextPassed = pageFrameContextGuardResult != null && pageFrameContextGuardResult.passed();

        if (!parseSuccessful) {
            blocking.add("Structured AI response failed to parse");
        }
        if (featureEmpty) {
            blocking.add("Generated feature file is empty");
        }
        if (!stepValidationPassed) {
            blocking.add("Step validation failed");
            if (stepReuseResult != null) {
                for (String step : stepReuseResult.unmatchedSteps()) {
                    blocking.add("Feature step is not reusable and not declared as new: " + step);
                }
            }
        }
        if (!yamlValidationPassed) {
            blocking.add("YAML validation failed");
        }
        if (!allowedYamlPassed) {
            blocking.add("Allowed YAML guard failed");
            if (allowedYamlGuardResult != null) {
                for (String key : allowedYamlGuardResult.unknownKeysUsed()) {
                    blocking.add("Unknown YAML key used: " + key);
                }
                for (String key : allowedYamlGuardResult.missingKeysUsedInFeature()) {
                    blocking.add("Missing YAML key appears inside feature file: " + key);
                }
                blocking.addAll(allowedYamlGuardResult.blockingErrors());
            }
        }
        if (!pageFrameContextPassed) {
            blocking.add("Page/frame context guard failed");
            if (pageFrameContextGuardResult != null) {
                blocking.addAll(pageFrameContextGuardResult.blockingErrors());
            }
        }

        if (stepReuseResult != null) {
            warnings.addAll(stepReuseResult.warnings());
        }
        if (yamlKeyResult != null) {
            warnings.addAll(yamlKeyResult.warnings());
        }
        if (allowedYamlGuardResult != null) {
            warnings.addAll(allowedYamlGuardResult.warnings());
        }
        if (pageFrameContextGuardResult != null) {
            warnings.addAll(pageFrameContextGuardResult.warnings());
        }

        List<String> uniqueBlocking = new ArrayList<>(new LinkedHashSet<>(blocking));
        List<String> uniqueWarnings = new ArrayList<>(new LinkedHashSet<>(warnings));

        return new RunnableFeatureResult(
                uniqueBlocking.isEmpty(),
                uniqueBlocking,
                uniqueWarnings,
                parseSuccessful,
                stepValidationPassed,
                yamlValidationPassed,
                allowedYamlPassed,
                pageFrameContextPassed,
                stepReuseResult != null ? stepReuseResult.reusePercentage() : 0.0,
                stepReuseResult != null ? stepReuseResult.totalSteps() : 0,
                stepReuseResult != null ? stepReuseResult.matchedCount() : 0,
                stepReuseResult != null ? stepReuseResult.unmatchedCount() : 0,
                yamlKeyResult != null ? yamlKeyResult.totalKeys() : 0,
                yamlKeyResult != null ? yamlKeyResult.existingCount() : 0,
                yamlKeyResult != null ? yamlKeyResult.missingCount() : 0,
                pageFrameContextGuardResult != null ? pageFrameContextGuardResult.frameStepCount() : 0,
                pageFrameContextGuardResult != null ? pageFrameContextGuardResult.pageStepCount() : 0
        );
    }
}

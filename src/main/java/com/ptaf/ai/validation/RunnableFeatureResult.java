package com.ptaf.ai.validation;

import java.util.List;

public record RunnableFeatureResult(
        boolean runnable,
        List<String> blockingReasons,
        List<String> warnings,
        boolean parseSuccessful,
        boolean stepValidationPassed,
        boolean yamlValidationPassed,
        boolean allowedYamlPassed,
        double stepReusePercentage,
        int totalSteps,
        int matchedSteps,
        int unmatchedSteps,
        int yamlKeysUsed,
        int existingYamlKeys,
        int missingYamlKeys
) {
}

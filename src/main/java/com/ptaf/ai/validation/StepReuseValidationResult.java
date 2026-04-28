package com.ptaf.ai.validation;

import java.util.List;

public record StepReuseValidationResult(
        List<String> featureSteps,
        List<String> matchedExistingSteps,
        List<String> unmatchedSteps,
        List<String> claimedReusedButNotFound,
        List<String> claimedNewButAlreadyExists,
        int totalSteps,
        int matchedCount,
        int unmatchedCount,
        double reusePercentage,
        boolean passed,
        List<String> warnings
) {
}

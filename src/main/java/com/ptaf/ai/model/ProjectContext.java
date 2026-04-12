package com.ptaf.ai.model;

import java.util.List;

/**
 * Ranked text slices for the prompt: features, step defs, extra framework files,
 * plus step patterns with scores (Phase 2).
 */
public record ProjectContext(
        String featuresSection,
        String stepDefinitionsSection,
        String frameworkSection,
        List<String> extractedStepPatterns,
        List<ScoredPattern> rankedStepPatterns
) {
}

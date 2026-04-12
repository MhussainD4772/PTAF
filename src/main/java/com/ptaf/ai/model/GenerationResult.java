package com.ptaf.ai.model;

import java.util.List;

/**
 * Model output plus Phase 2 local reuse trace (keyword-ranked patterns with source paths).
 */
public record GenerationResult(
        String featureGherkin,
        List<String> suggestedReusableSteps,
        String rawModelResponse,
        List<ScoredPattern> reuseTrace
) {
}

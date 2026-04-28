package com.ptaf.ai.model;

import com.ptaf.ai.validation.StepReuseValidationResult;
import com.ptaf.ai.validation.YamlKeyValidationResult;
import com.ptaf.ai.validation.AllowedYamlGuardResult;
import com.ptaf.ai.validation.MissingYamlPatchSuggestion;
import com.ptaf.ai.validation.PageFrameContextGuardResult;
import com.ptaf.ai.validation.RunnableFeatureResult;

import java.util.List;

/**
 * Model output plus Phase 2 local reuse trace (keyword-ranked patterns with source paths).
 */
public record GenerationResult(
        String featureGherkin,
        List<String> suggestedReusableSteps,
        String rawModelResponse,
        List<ScoredPattern> reuseTrace,
        AiGenerationStructuredResponse structuredResponse,
        StepReuseValidationResult stepReuseValidationResult,
        YamlKeyValidationResult yamlKeyValidationResult,
        AllowedYamlGuardResult allowedYamlGuardResult,
        PageFrameContextGuardResult pageFrameContextGuardResult,
        RunnableFeatureResult runnableFeatureResult,
        List<MissingYamlPatchSuggestion> missingYamlPatchSuggestions
) {
}

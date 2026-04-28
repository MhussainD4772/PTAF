package com.ptaf.ai;

import com.ptaf.ai.config.AiAssistantProperties;
import com.ptaf.ai.context.FrameworkGenerationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds a strict, context-grounded generation prompt.
 */
public final class PromptBuilder {

    private final AiAssistantProperties properties;

    public PromptBuilder(AiAssistantProperties properties) {
        this.properties = properties;
    }

    public String systemPrompt() {
        return """
                You are a senior test automation engineer for a Java + Cucumber + Playwright framework.
                Your job is to generate runnable feature content aligned to existing framework assets.
                """.strip();
    }

    public String userPrompt(
            String requirement,
            FrameworkGenerationContext context,
            List<String> similarFeatureSnippets
    ) {
        List<String> similarLimited = limit(similarFeatureSnippets, properties.contextMaxSimilarFeatures());
        List<String> stepsLimited = limit(context.existingStepDefinitions(), properties.contextMaxStepDefinitionsInPrompt());
        List<String> yamlLimited = limit(context.existingYamlKeys(), properties.contextMaxYamlKeysInPrompt());
        return """
                REQUIREMENT:
                %s

                SIMILAR_FEATURES:
                %s

                ALLOWED_STEP_DEFINITIONS:
                %s

                ALLOWED_YAML_KEYS:
                %s

                RULES:
                - You must reuse existing step definitions whenever possible.
                - You must use only YAML keys from ALLOWED_YAML_KEYS.
                - Do not invent YAML keys.
                - If a needed YAML key does not exist, list it in MISSING_YAML_KEYS.
                - Do not use a missing YAML key inside FEATURE_FILE as if it exists.
                - Prefer patterns from SIMILAR_FEATURES.
                - Return only the structured output contract.
                - No markdown outside the required contract.
                - Always include all contract sections, even if empty.
                - Gherkin must include a Feature and at least one Scenario.

                OUTPUT_CONTRACT:
                <<<FEATURE_FILE>>>
                Feature: ...
                  Scenario: ...
                    Given ...
                <<<END_FEATURE_FILE>>>
                <<<REUSED_STEPS>>>
                - one bullet per line
                <<<END_REUSED_STEPS>>>
                <<<NEW_STEPS_NEEDED>>>
                - one bullet per line
                <<<END_NEW_STEPS_NEEDED>>>
                <<<YAML_KEYS_USED>>>
                - elements.some.key
                <<<END_YAML_KEYS_USED>>>
                <<<MISSING_YAML_KEYS>>>
                - elements.missing.key
                <<<END_MISSING_YAML_KEYS>>>
                <<<WARNINGS>>>
                - warning text
                <<<END_WARNINGS>>>
                """.formatted(
                requirement.trim(),
                renderSection(similarLimited, "(none)"),
                renderSection(stepsLimited, "(none)"),
                renderSection(yamlLimited, "(none)")
        );
    }

    private static List<String> limit(List<String> items, int max) {
        if (items == null || items.isEmpty() || max <= 0) {
            return List.of();
        }
        if (items.size() <= max) {
            return items;
        }
        List<String> out = new ArrayList<>(items.subList(0, max));
        out.add("[TRUNCATED: showing " + max + " of " + items.size() + "]");
        return out;
    }

    private static String renderSection(List<String> lines, String emptyPlaceholder) {
        if (lines == null || lines.isEmpty()) {
            return emptyPlaceholder;
        }
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append("- ").append(line).append("\n");
        }
        return sb.toString().trim();
    }
}

package com.ptaf.ai;

import com.ptaf.ai.config.AiAssistantProperties;
import com.ptaf.ai.model.ProjectContext;
import com.ptaf.ai.model.ScoredPattern;

import java.util.List;

/**
 * Fixed templates so the model returns parseable sections; Phase 2 adds ranked framework context.
 */
public final class PromptBuilder {

    private final AiAssistantProperties properties;

    public PromptBuilder(AiAssistantProperties properties) {
        this.properties = properties;
    }

    public String systemPrompt() {
        return """
                You are a senior test automation engineer for a Java + Cucumber + Playwright framework.
                Draft NEW Gherkin that matches the team's style. Prefer reusing existing step wording from
                RANKED_STEP_PATTERNS_WITH_SOURCES when a step fits the requirement.

                Output rules:
                - Use the exact marker blocks requested in the user message.
                - Gherkin must include a Feature line and at least one Scenario with steps.
                - Steps are plain Gherkin only (Given/When/Then/And/But).
                - For suggested steps, prefer phrases that appear in the ranked pattern list.
                """.strip();
    }

    public String userPrompt(String requirement, ProjectContext ctx) {
        int budget = properties.maxTotalContextChars();
        String features = truncate(ctx.featuresSection(), (int) (budget * 0.24));
        String steps = truncate(ctx.stepDefinitionsSection(), (int) (budget * 0.24));
        String fw = truncate(ctx.frameworkSection(), (int) (budget * 0.38));
        String rankedPatternsBlock = formatRankedPatterns(ctx.rankedStepPatterns());

        return """
                REQUIREMENT / USER STORY:
                %s

                EXISTING_FEATURES (keyword-ranked excerpts):
                %s

                EXISTING_STEP_DEFINITIONS (keyword-ranked excerpts):
                %s

                FRAMEWORK_CONTEXT — hooks, page objects, element YAML, config (keyword-ranked excerpts):
                %s

                RANKED_STEP_PATTERNS_WITH_SOURCES (reuse these when possible; path = declaring file):
                %s

                Respond using EXACTLY this structure (no markdown code fences):
                <<<FEATURE_GHERKIN>>>
                Feature: ...
                  Scenario: ...
                    Given ...
                <<<END_FEATURE_GHERKIN>>>
                <<<SUGGESTED_REUSABLE_STEPS>>>
                - one bullet per line; when possible cite an existing step; you may add (source: path/to/File.java) after the step
                <<<END_SUGGESTED_REUSABLE_STEPS>>>
                """.formatted(
                requirement.trim(),
                features,
                steps,
                fw,
                rankedPatternsBlock
        );
    }

    private static String formatRankedPatterns(List<ScoredPattern> ranked) {
        if (ranked == null || ranked.isEmpty()) {
            return "(none)";
        }
        StringBuilder sb = new StringBuilder();
        for (ScoredPattern sp : ranked) {
            sb.append(String.format("%.1f", sp.score()))
                    .append(" | ")
                    .append(sp.sourceRelativePath())
                    .append(" | ")
                    .append(sp.pattern())
                    .append("\n");
        }
        return sb.toString();
    }

    private static String truncate(String text, int maxChars) {
        if (text == null || text.length() <= maxChars) {
            return text != null ? text : "";
        }
        return text.substring(0, maxChars) + "\n[TRUNCATED]\n";
    }
}

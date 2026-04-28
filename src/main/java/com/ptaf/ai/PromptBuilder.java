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
                - Return ONLY the required marker blocks; no markdown, no commentary, no extra text.
                - Always include all required sections, even if a section is empty.
                - Gherkin must include a Feature line and at least one Scenario with steps.
                - Steps are plain Gherkin only (Given/When/Then/And/But).
                - Prefer reusing existing steps from ranked patterns when possible.
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

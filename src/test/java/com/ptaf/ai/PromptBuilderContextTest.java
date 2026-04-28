package com.ptaf.ai;

import com.ptaf.ai.config.AiAssistantProperties;
import com.ptaf.ai.context.FrameworkGenerationContext;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromptBuilderContextTest {

    @Test
    void promptIncludesGroundedSectionsAndRules() {
        PromptBuilder builder = new PromptBuilder(props(3, 2, 2));
        FrameworkGenerationContext context = new FrameworkGenerationContext(
                List.of("Feature: Login\nScenario: Valid login"),
                List.of("user is on login page", "user clicks login"),
                List.of("elements.login.username", "elements.login.loginbutton"),
                List.of("elements.login.username"),
                List.of("api_requests.user.login"),
                List.of("queries.user.getById")
        );
        String prompt = builder.userPrompt(
                "User should login successfully",
                context,
                List.of("Feature: Existing Login\nScenario: Valid", "Feature: Other")
        );

        assertTrue(prompt.contains("REQUIREMENT:"));
        assertTrue(prompt.contains("SIMILAR_FEATURES:"));
        assertTrue(prompt.contains("ALLOWED_STEP_DEFINITIONS:"));
        assertTrue(prompt.contains("ALLOWED_YAML_KEYS:"));
        assertTrue(prompt.contains("UI_CONTEXT_RULES:"));
        assertTrue(prompt.contains("RULES:"));
        assertTrue(prompt.contains("OUTPUT_CONTRACT:"));

        assertTrue(prompt.contains("User should login successfully"));
        assertTrue(prompt.contains("Feature: Existing Login"));
        assertTrue(prompt.contains("user is on login page"));
        assertTrue(prompt.contains("elements.login.username"));
        assertTrue(prompt.contains("Do not invent YAML keys."));
        assertTrue(prompt.contains("Default to page steps."));
        assertTrue(prompt.contains("DEFAULT_UI_CONTEXT: page"));
        assertTrue(prompt.contains("FRAME_ALLOWED_PAGES:"));
        assertTrue(prompt.contains("paymentiframe"));
        assertTrue(prompt.contains("FRAME_ALLOWED_LOCATORS:"));
        assertTrue(prompt.contains("checkout.secureframe"));
        assertTrue(prompt.contains("list it in MISSING_YAML_KEYS"));

        assertTrue(prompt.contains("<<<FEATURE_FILE>>>"));
        assertTrue(prompt.contains("<<<END_FEATURE_FILE>>>"));
        assertTrue(prompt.contains("<<<REUSED_STEPS>>>"));
        assertTrue(prompt.contains("<<<END_REUSED_STEPS>>>"));
        assertTrue(prompt.contains("<<<NEW_STEPS_NEEDED>>>"));
        assertTrue(prompt.contains("<<<END_NEW_STEPS_NEEDED>>>"));
        assertTrue(prompt.contains("<<<YAML_KEYS_USED>>>"));
        assertTrue(prompt.contains("<<<END_YAML_KEYS_USED>>>"));
        assertTrue(prompt.contains("<<<MISSING_YAML_KEYS>>>"));
        assertTrue(prompt.contains("<<<END_MISSING_YAML_KEYS>>>"));
        assertTrue(prompt.contains("<<<WARNINGS>>>"));
        assertTrue(prompt.contains("<<<END_WARNINGS>>>"));
    }

    @Test
    void promptRespectsConfiguredLimits() {
        PromptBuilder builder = new PromptBuilder(props(1, 2, 2));
        FrameworkGenerationContext context = new FrameworkGenerationContext(
                List.of("Feature: A"),
                List.of("step one", "step two", "step three"),
                List.of("elements.a", "elements.b", "elements.c"),
                List.of(),
                List.of(),
                List.of()
        );
        List<String> similar = List.of("Feature: One", "Feature: Two", "Feature: Three");

        String prompt = builder.userPrompt("login", context, similar);

        assertTrue(prompt.contains("- Feature: One"));
        assertFalse(prompt.contains("- Feature: Two"));
        assertTrue(prompt.contains("[TRUNCATED: showing 1 of 3]"));
        assertTrue(prompt.contains("[TRUNCATED: showing 2 of 3]"));
    }

    @SuppressWarnings("unchecked")
    private static AiAssistantProperties props(int maxSimilar, int maxSteps, int maxYaml) {
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load("""
                ai_assistant:
                  context:
                    maxSimilarFeatures: %d
                    maxStepDefinitionsInPrompt: %d
                    maxYamlKeysInPrompt: %d
                  contextRules:
                    defaultUiContext: page
                    frameAllowedPages: [paymentIframe]
                    frameAllowedLocators: [checkout.secureFrame]
                """.formatted(maxSimilar, maxSteps, maxYaml));
        try {
            Constructor<AiAssistantProperties> ctor = AiAssistantProperties.class.getDeclaredConstructor(Map.class);
            ctor.setAccessible(true);
            return ctor.newInstance((Map<String, Object>) data);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}

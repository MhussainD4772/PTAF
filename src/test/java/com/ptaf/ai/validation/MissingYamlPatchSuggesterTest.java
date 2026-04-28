package com.ptaf.ai.validation;

import com.ptaf.ai.model.AiGenerationStructuredResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MissingYamlPatchSuggesterTest {
    private final MissingYamlPatchSuggester suggester = new MissingYamlPatchSuggester();

    @Test
    void uiElementPatchSuggestion() {
        List<MissingYamlPatchSuggestion> out = suggester.suggest(
                yamlWithMissing(List.of("elements.login.submitbutton")),
                null
        );
        assertEquals(1, out.size());
        assertEquals("src/test/resources/elements", out.get(0).targetFolder());
        assertTrue(out.get(0).suggestedYaml().contains("submitbutton: \"TODO_SELECTOR\""));
    }

    @Test
    void apiRequestPatchSuggestion() {
        List<MissingYamlPatchSuggestion> out = suggester.suggest(
                yamlWithMissing(List.of("api_requests.users.createuser")),
                null
        );
        assertEquals("src/test/resources/api_requests", out.get(0).targetFolder());
        assertTrue(out.get(0).suggestedYaml().contains("method: \"TODO_METHOD\""));
        assertTrue(out.get(0).suggestedYaml().contains("path: \"TODO_PATH\""));
    }

    @Test
    void dbQueryPatchSuggestion() {
        List<MissingYamlPatchSuggestion> out = suggester.suggest(
                yamlWithMissing(List.of("queries.user.getbyid")),
                null
        );
        assertEquals("src/test/resources/queries", out.get(0).targetFolder());
        assertTrue(out.get(0).suggestedYaml().contains("getbyid: \"TODO_SQL_QUERY\""));
    }

    @Test
    void configPatchSuggestion() {
        List<MissingYamlPatchSuggestion> out = suggester.suggest(
                yamlWithMissing(List.of("config.browser.timeout")),
                null
        );
        assertEquals("src/test/resources/config", out.get(0).targetFolder());
        assertTrue(out.get(0).suggestedYaml().contains("timeout: \"TODO_VALUE\""));
    }

    @Test
    void unknownCategoryWarning() {
        List<MissingYamlPatchSuggestion> out = suggester.suggest(
                yamlWithMissing(List.of("custom.x.y")),
                null
        );
        assertFalse(out.get(0).warnings().isEmpty());
    }

    @Test
    void multipleMissingKeys() {
        List<MissingYamlPatchSuggestion> out = suggester.suggest(
                yamlWithMissing(List.of("elements.a.b", "queries.q.r")),
                null
        );
        assertEquals(2, out.size());
    }

    @Test
    void emptyMissingListReturnsEmptySuggestions() {
        List<MissingYamlPatchSuggestion> out = suggester.suggest(
                yamlWithMissing(List.of()),
                null
        );
        assertTrue(out.isEmpty());
    }

    @Test
    void suggestionsDoNotMarkFeatureRunnable() {
        YamlKeyValidationResult yaml = yamlWithMissing(List.of("elements.login.submitbutton"));
        AllowedYamlGuardResult guard = new AllowedYamlGuardResult(
                false,
                List.of(),
                List.of("elements.login.submitbutton"),
                List.of(),
                List.of("elements.login.submitbutton"),
                List.of(),
                List.of("Unknown YAML key used: elements.login.submitbutton")
        );
        List<MissingYamlPatchSuggestion> out = suggester.suggest(yaml, guard);
        assertFalse(out.isEmpty());

        RunnableFeatureResult runnable = new RunnableFeatureGate().evaluate(
                structured(),
                step(),
                yaml,
                guard,
                new PageFrameContextGuardResult(true, List.of(), List.of(), List.of(), List.of(), 0, 1)
        );
        assertFalse(runnable.runnable());
    }

    private static YamlKeyValidationResult yamlWithMissing(List<String> missing) {
        return new YamlKeyValidationResult(
                List.of(),
                List.of(),
                missing,
                Map.of(),
                missing.size(),
                0,
                missing.size(),
                missing.isEmpty(),
                List.of()
        );
    }

    private static AiGenerationStructuredResponse structured() {
        AiGenerationStructuredResponse s = new AiGenerationStructuredResponse();
        s.setParseSuccessful(true);
        s.setFeatureFile("Feature: X\nScenario: Y\nThen elements.login.submitbutton");
        s.setParseErrors(List.of());
        s.setReusedSteps(List.of());
        s.setNewStepsNeeded(List.of());
        s.setYamlKeysUsed(List.of());
        s.setMissingYamlKeys(List.of("elements.login.submitbutton"));
        s.setWarnings(List.of());
        return s;
    }

    private static StepReuseValidationResult step() {
        return new StepReuseValidationResult(
                List.of("Given x"),
                List.of("Given x"),
                List.of(),
                List.of(),
                List.of(),
                1, 1, 0, 100.0, true, List.of()
        );
    }
}

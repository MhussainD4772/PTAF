package com.ptaf.ai.validation;

import com.ptaf.ai.index.YamlKeyIndex;
import com.ptaf.ai.model.AiGenerationStructuredResponse;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YamlKeyValidatorTest {

    private final YamlKeyValidator validator = new YamlKeyValidator();

    @Test
    void matchesExistingYamlKey() {
        AiGenerationStructuredResponse response = response(
                List.of("elements.login.username.input"),
                List.of()
        );
        YamlKeyValidationResult result = validator.validate(response, indexWith(
                Set.of("elements.login.username.input")
        ));

        assertEquals(1, result.existingCount());
        assertEquals(0, result.missingCount());
    }

    @Test
    void detectsMissingYamlKey() {
        AiGenerationStructuredResponse response = response(
                List.of("elements.login.submitbutton"),
                List.of()
        );
        YamlKeyValidationResult result = validator.validate(response, indexWith(
                Set.of("elements.login.username.input")
        ));

        assertEquals(1, result.missingCount());
        assertTrue(result.missingKeys().contains("elements.login.submitbutton"));
    }

    @Test
    void detectsAiReportedMissingKeyThatExists() {
        AiGenerationStructuredResponse response = response(
                List.of("elements.login.username.input"),
                List.of("elements.login.username.input")
        );
        YamlKeyValidationResult result = validator.validate(response, indexWith(
                Set.of("elements.login.username.input")
        ));

        assertTrue(result.warnings().stream().anyMatch(w -> w.contains("reported missing key that exists")));
    }

    @Test
    void detectsAiMissedMissingKey() {
        AiGenerationStructuredResponse response = response(
                List.of("queries.user.getbyid"),
                List.of()
        );
        YamlKeyValidationResult result = validator.validate(response, indexWith(
                Set.of("queries.user.getall")
        ));

        assertTrue(result.warnings().stream().anyMatch(w -> w.contains("AI missed missing YAML key")));
    }

    @Test
    void generatesUiLocatorPatchSuggestion() {
        AiGenerationStructuredResponse response = response(
                List.of("elements.login.submitbutton"),
                List.of()
        );
        YamlKeyValidationResult result = validator.validate(response, indexWith(Set.of()));
        String patch = result.suggestedPatches().get("elements.login.submitbutton");
        assertTrue(patch.contains("login:"));
        assertTrue(patch.contains("submitbutton: \"TODO_SELECTOR\""));
    }

    @Test
    void generatesApiPatchSuggestion() {
        AiGenerationStructuredResponse response = response(
                List.of("api_requests.users.createuser"),
                List.of()
        );
        YamlKeyValidationResult result = validator.validate(response, indexWith(Set.of()));
        String patch = result.suggestedPatches().get("api_requests.users.createuser");
        assertTrue(patch.contains("users:"));
        assertTrue(patch.contains("createuser:"));
        assertTrue(patch.contains("method: \"TODO_METHOD\""));
        assertTrue(patch.contains("path: \"TODO_PATH\""));
    }

    @Test
    void generatesDbQueryPatchSuggestion() {
        AiGenerationStructuredResponse response = response(
                List.of("queries.user.getbyid"),
                List.of()
        );
        YamlKeyValidationResult result = validator.validate(response, indexWith(Set.of()));
        String patch = result.suggestedPatches().get("queries.user.getbyid");
        assertTrue(patch.contains("user:"));
        assertTrue(patch.contains("getbyid: \"TODO_SQL_QUERY\""));
    }

    private static AiGenerationStructuredResponse response(List<String> used, List<String> missing) {
        AiGenerationStructuredResponse response = new AiGenerationStructuredResponse();
        response.setYamlKeysUsed(used);
        response.setMissingYamlKeys(missing);
        response.setFeatureFile("");
        response.setReusedSteps(List.of());
        response.setNewStepsNeeded(List.of());
        response.setWarnings(List.of());
        response.setParseErrors(List.of());
        response.setParseSuccessful(true);
        return response;
    }

    private static YamlKeyIndex indexWith(Set<String> keys) {
        Map<String, String> sourceTypes = new LinkedHashMap<>();
        for (String key : keys) {
            sourceTypes.put(key, "test");
        }
        return new YamlKeyIndex(new LinkedHashSet<>(keys), sourceTypes);
    }
}

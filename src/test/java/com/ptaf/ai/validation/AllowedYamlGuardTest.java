package com.ptaf.ai.validation;

import com.ptaf.ai.index.YamlKeyIndex;
import com.ptaf.ai.model.AiGenerationStructuredResponse;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AllowedYamlGuardTest {
    private final AllowedYamlGuard guard = new AllowedYamlGuard();

    @Test
    void passesWhenAllYamlKeysUsedExist() {
        AiGenerationStructuredResponse response = response(
                "Feature: X\nScenario: Y",
                List.of("elements.login.username"),
                List.of()
        );
        AllowedYamlGuardResult result = guard.validate(response, indexWith(Set.of("elements.login.username")));
        assertTrue(result.passed());
        assertTrue(result.blockingErrors().isEmpty());
    }

    @Test
    void failsWhenYamlKeysUsedContainsUnknownKey() {
        AiGenerationStructuredResponse response = response(
                "Feature: X\nScenario: Y",
                List.of("elements.login.submitbutton"),
                List.of()
        );
        AllowedYamlGuardResult result = guard.validate(response, indexWith(Set.of("elements.login.username")));
        assertFalse(result.passed());
        assertTrue(result.unknownKeysUsed().contains("elements.login.submitbutton"));
    }

    @Test
    void failsWhenMissingKeyAppearsInFeatureFile() {
        AiGenerationStructuredResponse response = response(
                "Feature: X\nScenario: Y\nWhen click elements.login.submitbutton",
                List.of(),
                List.of("elements.login.submitbutton")
        );
        AllowedYamlGuardResult result = guard.validate(response, indexWith(Set.of("elements.login.username")));
        assertFalse(result.passed());
        assertTrue(result.missingKeysUsedInFeature().contains("elements.login.submitbutton"));
    }

    @Test
    void detectsUnknownYamlLookingKeyInFeatureFile() {
        AiGenerationStructuredResponse response = response(
                "Feature: X\nScenario: Y\nThen use api_requests.users.create",
                List.of(),
                List.of()
        );
        AllowedYamlGuardResult result = guard.validate(response, indexWith(Set.of("api_requests.users.get")));
        assertFalse(result.passed());
        assertTrue(result.blockingErrors().stream().anyMatch(e -> e.contains("Unknown YAML-looking key")));
    }

    @Test
    void warnsWhenKnownKeyAppearsInFeatureButNotYamlKeysUsed() {
        AiGenerationStructuredResponse response = response(
                "Feature: X\nScenario: Y\nThen use elements.login.username",
                List.of(),
                List.of()
        );
        AllowedYamlGuardResult result = guard.validate(response, indexWith(Set.of("elements.login.username")));
        assertTrue(result.warnings().stream().anyMatch(w -> w.contains("not listed in YAML_KEYS_USED")));
    }

    @Test
    void handlesEmptyYamlKeysUsed() {
        AiGenerationStructuredResponse response = response("Feature: X", List.of(), List.of());
        AllowedYamlGuardResult result = guard.validate(response, indexWith(Set.of()));
        assertTrue(result.allowedKeysUsed().isEmpty());
    }

    @Test
    void handlesEmptyMissingYamlKeys() {
        AiGenerationStructuredResponse response = response("Feature: X", List.of("elements.login.username"), List.of());
        AllowedYamlGuardResult result = guard.validate(response, indexWith(Set.of("elements.login.username")));
        assertTrue(result.missingKeysDeclared().isEmpty());
    }

    @Test
    void normalizesKeysBeforeComparison() {
        AiGenerationStructuredResponse response = response(
                "Feature: X",
                List.of(" ELEMENTS.LOGIN.USERNAME "),
                List.of()
        );
        AllowedYamlGuardResult result = guard.validate(response, indexWith(Set.of("elements.login.username")));
        assertTrue(result.passed());
    }

    private static AiGenerationStructuredResponse response(String feature, List<String> used, List<String> missing) {
        AiGenerationStructuredResponse response = new AiGenerationStructuredResponse();
        response.setFeatureFile(feature);
        response.setYamlKeysUsed(used);
        response.setMissingYamlKeys(missing);
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

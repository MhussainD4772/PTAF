package com.ptaf.ai.validation;

import com.ptaf.ai.index.YamlKeyIndex;
import com.ptaf.ai.model.AiGenerationStructuredResponse;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AllowedYamlGuard {
    private static final Pattern YAML_KEY_PATTERN =
            Pattern.compile("(elements|api_requests|queries|config)\\.[a-zA-Z0-9_.-]+");

    public AllowedYamlGuardResult validate(
            AiGenerationStructuredResponse structuredResponse,
            YamlKeyIndex yamlKeyIndex
    ) {
        Set<String> known = yamlKeyIndex.normalizedKeys();
        Set<String> used = normalizeList(structuredResponse.yamlKeysUsed());
        Set<String> missingDeclared = normalizeList(structuredResponse.missingYamlKeys());
        Set<String> keysInFeature = extractYamlLikeKeys(structuredResponse.featureFile());

        List<String> allowed = new ArrayList<>();
        List<String> unknown = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<String> blocking = new ArrayList<>();
        List<String> missingUsedInFeature = new ArrayList<>();

        for (String key : used) {
            if (known.contains(key)) {
                allowed.add(key);
            } else {
                unknown.add(key);
                blocking.add("YAML key does not exist: " + key);
            }
        }

        for (String key : missingDeclared) {
            if (keysInFeature.contains(key)) {
                missingUsedInFeature.add(key);
                blocking.add("Missing YAML key cannot be used directly in FEATURE_FILE: " + key);
            }
        }

        for (String key : keysInFeature) {
            boolean isKnown = known.contains(key);
            boolean listedUsed = used.contains(key);
            if (!isKnown) {
                if (!unknown.contains(key)) {
                    unknown.add(key);
                }
                blocking.add("Unknown YAML-looking key used in FEATURE_FILE: " + key);
            } else if (!listedUsed) {
                warnings.add("Known YAML key appears in FEATURE_FILE but not listed in YAML_KEYS_USED: " + key);
            }
        }

        return new AllowedYamlGuardResult(
                blocking.isEmpty(),
                allowed,
                unknown,
                new ArrayList<>(missingDeclared),
                missingUsedInFeature,
                dedupe(warnings),
                dedupe(blocking)
        );
    }

    private static Set<String> normalizeList(List<String> keys) {
        Set<String> out = new LinkedHashSet<>();
        if (keys == null) {
            return out;
        }
        for (String key : keys) {
            String normalized = YamlKeyIndex.normalizeKey(key);
            if (!normalized.isBlank()) {
                out.add(normalized);
            }
        }
        return out;
    }

    private static Set<String> extractYamlLikeKeys(String featureFile) {
        Set<String> out = new LinkedHashSet<>();
        if (featureFile == null || featureFile.isBlank()) {
            return out;
        }
        Matcher matcher = YAML_KEY_PATTERN.matcher(featureFile);
        while (matcher.find()) {
            String key = YamlKeyIndex.normalizeKey(matcher.group());
            if (!key.isBlank()) {
                out.add(key);
            }
        }
        return out;
    }

    private static List<String> dedupe(List<String> values) {
        return new ArrayList<>(new LinkedHashSet<>(values));
    }
}

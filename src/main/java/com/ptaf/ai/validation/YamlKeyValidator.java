package com.ptaf.ai.validation;

import com.ptaf.ai.index.YamlKeyIndex;
import com.ptaf.ai.model.AiGenerationStructuredResponse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class YamlKeyValidator {

    public YamlKeyValidationResult validate(AiGenerationStructuredResponse structuredResponse, YamlKeyIndex yamlKeyIndex) {
        Set<String> usedNormalized = new LinkedHashSet<>();
        for (String key : structuredResponse.yamlKeysUsed()) {
            String normalized = YamlKeyIndex.normalizeKey(key);
            if (!normalized.isEmpty()) {
                usedNormalized.add(normalized);
            }
        }

        List<String> existing = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        for (String used : usedNormalized) {
            if (yamlKeyIndex.normalizedKeys().contains(used)) {
                existing.add(used);
            } else {
                missing.add(used);
            }
        }

        Set<String> aiMissingNormalized = new LinkedHashSet<>();
        for (String key : structuredResponse.missingYamlKeys()) {
            String normalized = YamlKeyIndex.normalizeKey(key);
            if (!normalized.isEmpty()) {
                aiMissingNormalized.add(normalized);
            }
        }

        List<String> warnings = new ArrayList<>();
        for (String aiMissing : aiMissingNormalized) {
            if (yamlKeyIndex.normalizedKeys().contains(aiMissing)) {
                warnings.add("AI reported missing key that exists: " + aiMissing);
            }
        }
        for (String actuallyMissing : missing) {
            if (!aiMissingNormalized.contains(actuallyMissing)) {
                warnings.add("AI missed missing YAML key: " + actuallyMissing);
            }
        }

        Map<String, String> suggestedPatches = new LinkedHashMap<>();
        for (String missingKey : missing) {
            suggestedPatches.put(missingKey, buildPatchSuggestion(missingKey));
        }

        int total = usedNormalized.size();
        int existingCount = existing.size();
        int missingCount = missing.size();
        boolean passed = missingCount == 0;

        return new YamlKeyValidationResult(
                new ArrayList<>(usedNormalized),
                existing,
                missing,
                suggestedPatches,
                total,
                existingCount,
                missingCount,
                passed,
                warnings
        );
    }

    private static String buildPatchSuggestion(String key) {
        String[] parts = key.split("\\.");
        if (parts.length < 2) {
            return "TODO_VALUE";
        }
        String namespace = parts[0].toLowerCase(Locale.ROOT);
        String[] path = new String[parts.length - 1];
        System.arraycopy(parts, 1, path, 0, path.length);

        if ("elements".equals(namespace)) {
            return buildLeafPatch(path, "\"TODO_SELECTOR\"");
        }
        if ("api_requests".equals(namespace)) {
            return buildApiPatch(path);
        }
        if ("queries".equals(namespace)) {
            return buildLeafPatch(path, "\"TODO_SQL_QUERY\"");
        }
        return buildLeafPatch(path, "\"TODO_VALUE\"");
    }

    private static String buildLeafPatch(String[] path, String leafValue) {
        StringBuilder out = new StringBuilder();
        if (path.length == 0) {
            return """
                    method: "TODO_METHOD"
                    path: "TODO_PATH"
                    headers: {}
                    body: {}
                    """.trim();
        }
        for (int i = 0; i < path.length; i++) {
            indent(out, i).append(path[i]).append(":");
            if (i == path.length - 1) {
                out.append(" ").append(leafValue).append("\n");
            } else {
                out.append("\n");
            }
        }
        return out.toString().trim();
    }

    private static String buildApiPatch(String[] path) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < path.length; i++) {
            indent(out, i).append(path[i]).append(":");
            if (i == path.length - 1) {
                out.append("\n");
                indent(out, i + 1).append("method: \"TODO_METHOD\"\n");
                indent(out, i + 1).append("path: \"TODO_PATH\"\n");
                indent(out, i + 1).append("headers: {}\n");
                indent(out, i + 1).append("body: {}\n");
            } else {
                out.append("\n");
            }
        }
        return out.toString().trim();
    }

    private static StringBuilder indent(StringBuilder builder, int level) {
        builder.append("  ".repeat(Math.max(0, level)));
        return builder;
    }
}

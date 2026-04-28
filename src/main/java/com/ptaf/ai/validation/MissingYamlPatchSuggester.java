package com.ptaf.ai.validation;

import com.ptaf.ai.index.YamlKeyIndex;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class MissingYamlPatchSuggester {

    public List<MissingYamlPatchSuggestion> suggest(
            YamlKeyValidationResult yamlKeyValidationResult,
            AllowedYamlGuardResult allowedYamlGuardResult
    ) {
        Set<String> missingKeys = new LinkedHashSet<>();
        if (yamlKeyValidationResult != null) {
            missingKeys.addAll(yamlKeyValidationResult.missingKeys());
        }
        if (allowedYamlGuardResult != null) {
            missingKeys.addAll(allowedYamlGuardResult.unknownKeysUsed());
            missingKeys.addAll(allowedYamlGuardResult.missingKeysUsedInFeature());
        }

        List<MissingYamlPatchSuggestion> out = new ArrayList<>();
        for (String rawKey : missingKeys) {
            String key = YamlKeyIndex.normalizeKey(rawKey);
            if (key.isBlank()) {
                continue;
            }
            out.add(buildSuggestion(key));
        }
        return out;
    }

    private static MissingYamlPatchSuggestion buildSuggestion(String key) {
        String[] parts = key.split("\\.");
        if (parts.length < 2) {
            return new MissingYamlPatchSuggestion(
                    key,
                    "unknown",
                    "unknown",
                    "TODO_VALUE",
                    List.of("Unknown YAML key category; manual patch required")
            );
        }

        String category = parts[0];
        String[] path = new String[parts.length - 1];
        System.arraycopy(parts, 1, path, 0, path.length);

        return switch (category) {
            case "elements" -> new MissingYamlPatchSuggestion(
                    key,
                    "elements",
                    "src/test/resources/elements",
                    buildLeafPatch(path, "\"TODO_SELECTOR\""),
                    List.of()
            );
            case "api_requests" -> new MissingYamlPatchSuggestion(
                    key,
                    "api_requests",
                    "src/test/resources/api_requests",
                    buildApiPatch(path),
                    List.of()
            );
            case "queries" -> new MissingYamlPatchSuggestion(
                    key,
                    "queries",
                    "src/test/resources/queries",
                    buildLeafPatch(path, "\"TODO_SQL_QUERY\""),
                    List.of()
            );
            case "config" -> new MissingYamlPatchSuggestion(
                    key,
                    "config",
                    "src/test/resources/config",
                    buildLeafPatch(path, "\"TODO_VALUE\""),
                    List.of()
            );
            default -> new MissingYamlPatchSuggestion(
                    key,
                    category,
                    "unknown",
                    buildLeafPatch(path, "\"TODO_VALUE\""),
                    List.of("Unknown YAML key category; verify target folder manually")
            );
        };
    }

    private static String buildLeafPatch(String[] path, String leafValue) {
        if (path.length == 0) {
            return "value: " + leafValue;
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < path.length; i++) {
            out.append("  ".repeat(Math.max(0, i))).append(path[i]).append(":");
            if (i == path.length - 1) {
                out.append(" ").append(leafValue).append("\n");
            } else {
                out.append("\n");
            }
        }
        return out.toString().trim();
    }

    private static String buildApiPatch(String[] path) {
        if (path.length == 0) {
            return """
                    method: "TODO_METHOD"
                    path: "TODO_PATH"
                    headers: {}
                    body: {}
                    """.trim();
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < path.length; i++) {
            out.append("  ".repeat(Math.max(0, i))).append(path[i]).append(":");
            if (i == path.length - 1) {
                out.append("\n");
                out.append("  ".repeat(i + 1)).append("method: \"TODO_METHOD\"\n");
                out.append("  ".repeat(i + 1)).append("path: \"TODO_PATH\"\n");
                out.append("  ".repeat(i + 1)).append("headers: {}\n");
                out.append("  ".repeat(i + 1)).append("body: {}\n");
            } else {
                out.append("\n");
            }
        }
        return out.toString().trim();
    }
}

package com.ptaf.ai.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Loads {@code /config/ai_assistant.yml} from the classpath.
 * API key: {@code System.getenv(gemini_api_key_env)} first, else {@code .env} in the working directory (see {@link LocalDotEnv}).
 */
public final class AiAssistantProperties {

    private final Map<String, Object> root;

    public AiAssistantProperties() {
        this(loadYaml());
    }

    AiAssistantProperties(Map<String, Object> root) {
        this.root = root;
    }

    private static Map<String, Object> loadYaml() {
        try (InputStream in = AiAssistantProperties.class.getResourceAsStream("/config/ai_assistant.yml")) {
            if (in == null) {
                return Collections.emptyMap();
            }
            Yaml yaml = new Yaml();
            @SuppressWarnings("unchecked")
            Map<String, Object> map = yaml.load(in);
            return map != null ? map : Collections.emptyMap();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load /config/ai_assistant.yml", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> ai() {
        Object v = root.get("ai_assistant");
        if (v instanceof Map<?, ?> m) {
            return (Map<String, Object>) m;
        }
        return Collections.emptyMap();
    }

    private String str(String key, String defaultValue) {
        Object v = ai().get(key);
        return v != null ? Objects.toString(v, defaultValue) : defaultValue;
    }

    private double dbl(String key, double defaultValue) {
        Object v = ai().get(key);
        if (v instanceof Number n) {
            return n.doubleValue();
        }
        if (v instanceof String s) {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private int integer(String key, int defaultValue) {
        Object v = ai().get(key);
        if (v instanceof Number n) {
            return n.intValue();
        }
        if (v instanceof String s) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public String geminiApiKeyEnvName() {
        return str("gemini_api_key_env", "GEMINI_API_KEY");
    }

    public String apiKey() {
        String name = geminiApiKeyEnvName();
        try {
            String v = System.getenv(name);
            if (v != null && !v.isBlank()) {
                return v;
            }
        } catch (SecurityException ignored) {
            // fall through to .env
        }
        String fromFile = LocalDotEnv.get(name);
        return fromFile != null ? fromFile : "";
    }

    public String model() {
        String fromEnv = getenv("GEMINI_MODEL");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim();
        }
        String fromDotEnv = LocalDotEnv.get("GEMINI_MODEL");
        if (fromDotEnv != null && !fromDotEnv.isBlank()) {
            return fromDotEnv.trim();
        }
        return str("model", "gemini-2.5-flash");
    }

    public String featuresDir() {
        return str("features_dir", "src/test/resources/features");
    }

    public String stepDefinitionsDir() {
        return str("step_definitions_dir", "src/test/java/com/ptaf/stepdefinitions");
    }

    public List<String> stepDefinitionPaths() {
        Object value = ai().get("step_definition_paths");
        if (value instanceof List<?> list) {
            return list.stream()
                    .filter(item -> item != null && !Objects.toString(item, "").isBlank())
                    .map(item -> Objects.toString(item, "").trim())
                    .toList();
        }
        return List.of(stepDefinitionsDir());
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> yamlPaths() {
        Map<String, String> defaults = new LinkedHashMap<>();
        defaults.put("elements", "src/test/resources/elements");
        defaults.put("api_requests", "src/test/resources/api_requests");
        defaults.put("queries", "src/test/resources/queries");
        defaults.put("config", "src/test/resources/config");

        Object value = ai().get("yaml_paths");
        if (!(value instanceof Map<?, ?> map)) {
            return defaults;
        }
        Map<String, String> out = new LinkedHashMap<>(defaults);
        for (Map.Entry<?, ?> e : map.entrySet()) {
            String key = Objects.toString(e.getKey(), "").trim();
            String path = Objects.toString(e.getValue(), "").trim();
            if (!key.isEmpty() && !path.isEmpty()) {
                out.put(key, path);
            }
        }
        return out;
    }

    public int maxFeatureFiles() {
        return integer("max_feature_files", 8);
    }

    public int maxStepDefFiles() {
        return integer("max_step_def_files", 8);
    }

    public String hooksDir() {
        return str("hooks_dir", "src/main/java/com/ptaf/hooks");
    }

    public String uiPagesDir() {
        return str("ui_pages_dir", "src/main/java/com/ptaf/ui/pages");
    }

    public String elementsDir() {
        return str("elements_dir", "src/test/resources/elements");
    }

    public String configYamlDir() {
        return str("config_yaml_dir", "src/test/resources/config");
    }

    public int maxHooksFiles() {
        return integer("max_hooks_files", 8);
    }

    public int maxPagesFiles() {
        return integer("max_pages_files", 16);
    }

    public int maxElementsFiles() {
        return integer("max_elements_files", 20);
    }

    public int maxConfigYamlFiles() {
        return integer("max_config_yaml_files", 10);
    }

    public int rankingTopChunks() {
        return integer("ranking_top_chunks", 14);
    }

    public int rankingTopPatterns() {
        return integer("ranking_top_patterns", 55);
    }

    public int maxTotalContextChars() {
        return integer("max_total_context_chars", 100_000);
    }

    public double temperature() {
        return dbl("temperature", 0.2);
    }

    public int maxOutputTokens() {
        return integer("max_output_tokens", 8192);
    }

    public String promptVersion() {
        return str("prompt_version", "phase1-v1");
    }

    @SuppressWarnings("unchecked")
    public boolean auditEnabled() {
        Object auditObj = ai().get("audit");
        if (auditObj instanceof Map<?, ?> map) {
            Object enabled = map.get("enabled");
            if (enabled instanceof Boolean b) {
                return b;
            }
            if (enabled instanceof String s) {
                return Boolean.parseBoolean(s.trim());
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public String auditOutputPath() {
        Object auditObj = ai().get("audit");
        if (auditObj instanceof Map<?, ?> map) {
            Object output = map.get("output_path");
            if (output != null && !Objects.toString(output, "").isBlank()) {
                return Objects.toString(output, "target/ai-audit/generation-audit.jsonl").trim();
            }
        }
        return "target/ai-audit/generation-audit.jsonl";
    }

    private static String getenv(String name) {
        try {
            return System.getenv(name);
        } catch (SecurityException e) {
            return null;
        }
    }
}

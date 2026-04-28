package com.ptaf.ai.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads {@code .env} from the process working directory (project root when using Maven exec).
 * Does not override real environment variables — use {@link AiAssistantProperties} for merge order.
 */
final class LocalDotEnv {

    private static final Map<String, String> ENTRIES = load();

    private LocalDotEnv() {
    }

    static String get(String key) {
        if (key == null) {
            return null;
        }
        return ENTRIES.get(key);
    }

    private static Map<String, String> load() {
        Path p = Path.of(System.getProperty("user.dir", ".")).resolve(".env").normalize();
        if (!Files.isRegularFile(p)) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>();
        try {
            for (String line : Files.readAllLines(p, StandardCharsets.UTF_8)) {
                String t = line.trim();
                if (t.isEmpty() || t.startsWith("#")) {
                    continue;
                }
                int eq = t.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                String k = t.substring(0, eq).trim();
                String v = t.substring(eq + 1).trim();
                if (v.startsWith("\"") && v.endsWith("\"") && v.length() >= 2) {
                    v = v.substring(1, v.length() - 1);
                } else if (v.startsWith("'") && v.endsWith("'") && v.length() >= 2) {
                    v = v.substring(1, v.length() - 1);
                }
                if (!k.isEmpty()) {
                    map.put(k, v);
                }
            }
        } catch (IOException e) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(map);
    }
}

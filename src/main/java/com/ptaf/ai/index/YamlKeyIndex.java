package com.ptaf.ai.index;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Indexes YAML keys across configured framework folders and flattens nested keys into dot notation.
 */
public final class YamlKeyIndex {
    private final Set<String> normalizedKeys;
    private final Map<String, String> keySourceType;

    public YamlKeyIndex(Set<String> normalizedKeys, Map<String, String> keySourceType) {
        this.normalizedKeys = Set.copyOf(normalizedKeys);
        this.keySourceType = Map.copyOf(keySourceType);
    }

    public Set<String> normalizedKeys() {
        return normalizedKeys;
    }

    public Map<String, String> keySourceType() {
        return keySourceType;
    }

    public static YamlKeyIndex build(Path projectRoot, Map<String, String> yamlPaths) throws IOException {
        Set<String> keys = new LinkedHashSet<>();
        Map<String, String> sourceType = new LinkedHashMap<>();
        Yaml yaml = new Yaml();

        for (Map.Entry<String, String> entry : yamlPaths.entrySet()) {
            String namespace = entry.getKey();
            Path root = projectRoot.resolve(entry.getValue()).normalize();
            if (!Files.isDirectory(root)) {
                continue;
            }
            for (Path file : listYamlFiles(root)) {
                String text = Files.readString(file, StandardCharsets.UTF_8);
                Object parsed = yaml.load(text);
                flatten(namespace, "", parsed, keys, sourceType);
            }
        }

        return new YamlKeyIndex(keys, sourceType);
    }

    @SuppressWarnings("unchecked")
    private static void flatten(
            String namespace,
            String currentPath,
            Object value,
            Set<String> keys,
            Map<String, String> sourceType
    ) {
        if (value instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = String.valueOf(entry.getKey());
                String nextPath = currentPath.isEmpty() ? key : currentPath + "." + key;
                String fullKey = normalizeKey(namespace + "." + nextPath);
                keys.add(fullKey);
                sourceType.putIfAbsent(fullKey, namespace);
                flatten(namespace, nextPath, entry.getValue(), keys, sourceType);
            }
            return;
        }
        if (value instanceof List<?> list) {
            for (int i = 0; i < list.size(); i++) {
                String nextPath = currentPath + "." + i;
                String fullKey = normalizeKey(namespace + "." + nextPath);
                keys.add(fullKey);
                sourceType.putIfAbsent(fullKey, namespace);
                flatten(namespace, nextPath, list.get(i), keys, sourceType);
            }
        }
    }

    public static String normalizeKey(String raw) {
        if (raw == null) {
            return "";
        }
        String normalized = raw.trim().toLowerCase();
        normalized = normalized.replaceAll("\\.+", ".");
        normalized = normalized.replaceAll("^\\.+", "");
        normalized = normalized.replaceAll("\\.+$", "");
        return normalized;
    }

    private static List<Path> listYamlFiles(Path root) throws IOException {
        try (Stream<Path> walk = Files.walk(root)) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String n = path.toString().toLowerCase();
                        return n.endsWith(".yml") || n.endsWith(".yaml");
                    })
                    .sorted(Comparator.comparing(Path::toString))
                    .collect(Collectors.toCollection(ArrayList::new));
        }
    }
}

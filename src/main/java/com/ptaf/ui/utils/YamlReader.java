package com.ptaf.ui.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * YamlReader is a utility class designed for loading and managing configuration
 * data stored in YAML files. It now reads all YAML files from a list of specified folders,
 * merges the data into a single map, and provides methods for retrieving values
 * based on dot-separated keys.
 */
public class YamlReader {
    // Static map to hold all the merged YAML data
    private static final Map<String, Object> data = new HashMap<>();

    // Static block to load YAML files during class initialization
    static {
        String[] folderPaths = {"elements", "queries"}; // Add "queries" or any other folder here

        Yaml yaml = new Yaml();

        for (String folderPath : folderPaths) {
            try {
                URL resourceUrl = YamlReader.class.getClassLoader().getResource(folderPath);
                if (resourceUrl == null) {
                    System.err.println("WARNING: Configuration folder not found in resources: " + folderPath);
                    continue; // Skip to the next folder
                }

                try (Stream<Path> paths = Files.walk(Paths.get(resourceUrl.toURI()))) {
                    paths
                            .filter(Files::isRegularFile)
                            .filter(path -> path.toString().endsWith(".yml") || path.toString().endsWith(".yaml"))
                            .forEach(path -> {
                                try (InputStream inputStream = Files.newInputStream(path)) {
                                    Map<String, Object> fileData = yaml.load(inputStream);
                                    if (fileData != null) {
                                        mergeData(data, fileData);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                }
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Merges new data into the base map recursively.
     * (This method is unchanged)
     */
    private static void mergeData(Map<String, Object> base, Map<String, Object> newData) {
        for (Map.Entry<String, Object> entry : newData.entrySet()) {
            if (base.containsKey(entry.getKey()) && base.get(entry.getKey()) instanceof Map && entry.getValue() instanceof Map) {
                mergeData((Map<String, Object>) base.get(entry.getKey()), (Map<String, Object>) entry.getValue());
            } else {
                base.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Retrieves a value from the loaded YAML data based on a dot-separated key.
     * (This method is unchanged)
     */
    public static Object get(String key) {
        String[] keys = key.split("\\.");
        Map<String, Object> currentMap = data;

        for (int i = 0; i < keys.length - 1; i++) {
            Object value = currentMap.get(keys[i]);
            if (value instanceof Map) {
                currentMap = (Map<String, Object>) value;
            } else {
                return null; // Key path does not exist
            }
        }
        return currentMap.get(keys[keys.length - 1]);
    }
}
package com.ptaf.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * YamlReader is a utility class designed for loading and managing configuration
 * data stored in YAML files. It reads all YAML files from a specified folder,
 * merges the data into a single map, and provides methods for retrieving values
 * based on dot-separated keys.
 */
public class YamlReader {
    // Static map to hold the loaded YAML data
    private static final Map<String, Object> data = new HashMap<>();

    // Static block to load YAML files from the specified folder during class initialization
    static {
        // Create a new Yaml instance for parsing YAML files
        Yaml yaml = new Yaml();
        // Specify the folder path where the YAML files are located
        String folderPath = "elements";

        try (Stream<Path> paths = Files.walk(Paths.get(YamlReader.class.getClassLoader().getResource(folderPath).toURI()))) {
            // Filter and process each file in the folder
            paths
                    .filter(Files::isRegularFile)  // Only process files that are regular files
                    .filter(path -> path.toString().endsWith(".yml"))  // Limit processing to YAML files
                    .forEach(path -> {
                        try (InputStream inputStream = Files.newInputStream(path)) {
                            // Load the data from the YAML file
                            Map<String, Object> fileData = yaml.load(inputStream);
                            // Merge the loaded data into the main data map
                            mergeData(data, fileData);
                        } catch (IOException e) {
                            // Print the stack trace in case of an error
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            // Print the stack trace in case of an error with the directory or file access
            e.printStackTrace();
        }
    }

    /**
     * Merges new data into the base map.
     *
     * @param base The base map into which new data will be merged
     * @param newData The new data map that needs to be merged with the base map
     */
    private static void mergeData(Map<String, Object> base, Map<String, Object> newData) {
        // Iterate over the entries in the new data map
        for (Map.Entry<String, Object> entry : newData.entrySet()) {
            // If the base map already contains the key, decide how to merge the values
            if (base.containsKey(entry.getKey())) {
                Object baseValue = base.get(entry.getKey());
                Object newValue = entry.getValue();
                // If both values are maps, merge them recursively
                if (baseValue instanceof Map && newValue instanceof Map) {
                    mergeData((Map<String, Object>) baseValue, (Map<String, Object>) newValue);
                } else {
                    // Otherwise, replace the existing value in the base map with the new value
                    base.put(entry.getKey(), newValue);
                }
            } else {
                // If the key is not present in the base map, add the new entry
                base.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Retrieves a value from the loaded YAML data based on a dot-separated key.
     *
     * @param key The dot-separated key representing the path to the desired value
     * @return The value associated with the key, or null if not found
     */
    public static Object get(String key) {
        // Split the key into parts using the dot as a delimiter
        String[] keys = key.split("\\.");
        Map<String, Object> value = data;

        // Traverse the map using the segments of the key
        for (int i = 0; i < keys.length - 1; i++) {
            value = (Map<String, Object>) value.get(keys[i]);
            // If at any point, the key does not exist, stop and return null
            if (value == null) {
                return null;
            }
        }

        // Return the final value for the last key segment
        return value.get(keys[keys.length - 1]);
    }
}
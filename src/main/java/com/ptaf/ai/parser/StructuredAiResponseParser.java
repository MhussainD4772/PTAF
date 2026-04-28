package com.ptaf.ai.parser;

import com.ptaf.ai.model.AiGenerationStructuredResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses strict marker-based output into a structured response.
 */
public final class StructuredAiResponseParser {
    private static final String FEATURE_FILE = "FEATURE_FILE";
    private static final String REUSED_STEPS = "REUSED_STEPS";
    private static final String NEW_STEPS_NEEDED = "NEW_STEPS_NEEDED";
    private static final String YAML_KEYS_USED = "YAML_KEYS_USED";
    private static final String MISSING_YAML_KEYS = "MISSING_YAML_KEYS";
    private static final String WARNINGS = "WARNINGS";

    private StructuredAiResponseParser() {
    }

    public static AiGenerationStructuredResponse parse(String raw) {
        AiGenerationStructuredResponse response = new AiGenerationStructuredResponse();
        List<String> errors = new ArrayList<>();

        response.setFeatureFile(extractTextSection(raw, FEATURE_FILE, errors));
        response.setReusedSteps(parseBulletSection(raw, REUSED_STEPS, errors));
        response.setNewStepsNeeded(parseBulletSection(raw, NEW_STEPS_NEEDED, errors));
        response.setYamlKeysUsed(parseBulletSection(raw, YAML_KEYS_USED, errors));
        response.setMissingYamlKeys(parseBulletSection(raw, MISSING_YAML_KEYS, errors));
        response.setWarnings(parseBulletSection(raw, WARNINGS, errors));

        response.setParseErrors(errors);
        response.setParseSuccessful(errors.isEmpty());
        return response;
    }

    private static String extractTextSection(String raw, String name, List<String> errors) {
        String block = extractBlock(raw, name, errors);
        return block == null ? "" : block.trim();
    }

    private static List<String> parseBulletSection(String raw, String name, List<String> errors) {
        String block = extractBlock(raw, name, errors);
        if (block == null || block.isBlank()) {
            return new ArrayList<>();
        }
        List<String> values = new ArrayList<>();
        for (String line : block.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("- ")) {
                String value = trimmed.substring(2).trim();
                if (!value.isEmpty()) {
                    values.add(value);
                }
            } else if (trimmed.startsWith("* ")) {
                String value = trimmed.substring(2).trim();
                if (!value.isEmpty()) {
                    values.add(value);
                }
            }
        }
        return values;
    }

    private static String extractBlock(String raw, String name, List<String> errors) {
        if (raw == null) {
            errors.add("Raw response is null");
            return null;
        }
        Pattern pattern = Pattern.compile(
                "<<<" + Pattern.quote(name) + ">>>(.*?)<<<END_" + Pattern.quote(name) + ">>>",
                Pattern.DOTALL
        );
        Matcher matcher = pattern.matcher(raw);
        if (!matcher.find()) {
            errors.add("Missing required section: " + name);
            return null;
        }
        return matcher.group(1);
    }
}

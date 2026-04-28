package com.ptaf.ai.quality;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** Phase 3: lightweight structural checks on a single feature file (no full Gherkin parser). */
public final class BasicGherkinValidator {

    private static final Pattern FEATURE = Pattern.compile("^\\s*Feature:\\s*\\S+", Pattern.CASE_INSENSITIVE);
    private static final Pattern SCENARIO = Pattern.compile("^\\s*(Scenario|Scenario Outline):\\s*\\S+", Pattern.CASE_INSENSITIVE);

    private BasicGherkinValidator() {
    }

    public static List<String> validate(String relativePath, String content) {
        List<String> issues = new ArrayList<>();
        String[] lines = content.split("\\R");
        boolean hasFeature = false;
        boolean hasScenario = false;
        for (String line : lines) {
            if (FEATURE.matcher(line).find()) {
                hasFeature = true;
            }
            if (SCENARIO.matcher(line).find()) {
                hasScenario = true;
            }
        }
        if (!hasFeature) {
            issues.add(relativePath + ": missing Feature: line");
        }
        if (!hasScenario) {
            issues.add(relativePath + ": missing Scenario / Scenario Outline");
        }
        return issues;
    }
}

package com.ptaf.ai;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pulls Cucumber annotation strings from Java source, e.g. {@code @Then("^we click...$")}.
 */
public final class StepPatternExtractor {

    private static final Pattern ANNOTATION = Pattern.compile(
            "@(Given|When|Then|And|But)\\(\\s*\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\"\\s*\\)");

    private StepPatternExtractor() {
    }

    public static List<String> fromJavaSource(String javaSource) {
        Set<String> ordered = new LinkedHashSet<>();
        Matcher m = ANNOTATION.matcher(javaSource);
        while (m.find()) {
            String pattern = m.group(2);
            if (pattern != null && !pattern.isBlank()) {
                ordered.add(pattern.trim());
            }
        }
        return new ArrayList<>(ordered);
    }
}

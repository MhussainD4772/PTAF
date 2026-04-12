package com.ptaf.ai.quality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Phase 3: finds frequently repeated step lines across {@code .feature} files (normalized text).
 */
public final class DuplicateStepDetector {

    private static final Pattern STEP_LINE = Pattern.compile(
            "^\\s*(Given|When|Then|And|But)\\s+(.+?)\\s*$",
            Pattern.CASE_INSENSITIVE
    );

    private DuplicateStepDetector() {
    }

    public static String normalize(String stepBody) {
        if (stepBody == null) {
            return "";
        }
        String t = stepBody.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        return t;
    }

    public static List<StepOccurrence> scanFile(String relativePath, List<String> lines) {
        List<StepOccurrence> out = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            Matcher m = STEP_LINE.matcher(lines.get(i));
            if (m.matches()) {
                String body = m.group(2);
                String norm = normalize(body);
                if (!norm.isEmpty()) {
                    out.add(new StepOccurrence(relativePath, i + 1, lines.get(i).trim(), norm));
                }
            }
        }
        return out;
    }

    /**
     * Groups by normalized text; lists occurrences when count &gt;= warnThreshold.
     */
    public static List<DuplicateGroup> findDuplicates(List<StepOccurrence> all, int warnThreshold) {
        Map<String, List<StepOccurrence>> byNorm = new HashMap<>();
        for (StepOccurrence o : all) {
            byNorm.computeIfAbsent(o.normalized(), k -> new ArrayList<>()).add(o);
        }
        List<DuplicateGroup> groups = new ArrayList<>();
        for (Map.Entry<String, List<StepOccurrence>> e : byNorm.entrySet()) {
            if (e.getValue().size() >= warnThreshold) {
                groups.add(new DuplicateGroup(e.getKey(), e.getValue()));
            }
        }
        groups.sort((a, b) -> Integer.compare(b.occurrences().size(), a.occurrences().size()));
        return groups;
    }

    public record DuplicateGroup(String normalizedStep, List<StepOccurrence> occurrences) {
        public int count() {
            return occurrences.size();
        }
    }
}

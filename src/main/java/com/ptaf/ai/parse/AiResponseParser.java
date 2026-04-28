package com.ptaf.ai.parse;

import com.ptaf.ai.model.GenerationResult;
import com.ptaf.ai.model.AiGenerationStructuredResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Splits model text between <<<FEATURE_GHERKIN>>> and <<<SUGGESTED_REUSABLE_STEPS>>> markers. */
public final class AiResponseParser {

    private static final Pattern FEATURE = Pattern.compile(
            "<<<FEATURE_GHERKIN>>>(.*?)<<<END_FEATURE_GHERKIN>>>",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE
    );
    private static final Pattern STEPS = Pattern.compile(
            "<<<SUGGESTED_REUSABLE_STEPS>>>(.*?)<<<END_SUGGESTED_REUSABLE_STEPS>>>",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE
    );

    private AiResponseParser() {
    }

    public static GenerationResult parse(String raw) {
        String feature = extract(FEATURE, raw);
        String suggestedBlock = extract(STEPS, raw);
        if (feature == null || feature.isBlank()) {
            feature = fallbackFeature(raw);
        }
        List<String> suggestions = parseBulletList(suggestedBlock != null ? suggestedBlock : "");
        return new GenerationResult(
                feature != null ? feature.trim() : "",
                suggestions,
                raw,
                Collections.emptyList(),
                new AiGenerationStructuredResponse(),
                null,
                null,
                null,
                null,
                null,
                List.of()
        );
    }

    private static String extract(Pattern p, String raw) {
        if (raw == null) {
            return null;
        }
        Matcher m = p.matcher(raw);
        return m.find() ? m.group(1) : null;
    }

    private static String fallbackFeature(String raw) {
        int idx = raw.indexOf("Feature:");
        if (idx >= 0) {
            return raw.substring(idx).trim();
        }
        return raw != null ? raw.trim() : "";
    }

    private static List<String> parseBulletList(String block) {
        List<String> out = new ArrayList<>();
        if (block == null || block.isBlank()) {
            return out;
        }
        for (String line : block.split("\\R")) {
            String t = line.trim();
            if (t.startsWith("- ")) {
                out.add(t.substring(2).trim());
            } else if (t.startsWith("* ")) {
                out.add(t.substring(2).trim());
            } else if (!t.isEmpty() && Character.isLetter(t.charAt(0))) {
                out.add(t);
            }
        }
        return out;
    }
}

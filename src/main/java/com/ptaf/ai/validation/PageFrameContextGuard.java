package com.ptaf.ai.validation;

import com.ptaf.ai.model.AiGenerationStructuredResponse;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PageFrameContextGuard {
    private static final Pattern PAGE_FRAME_STEP = Pattern.compile(
            ".*on\\s+(page|frame)\\s+([a-zA-Z0-9_.-]+)\\s+(?:locator|of\\s+locator)\\s+([a-zA-Z0-9_.-]+).*",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern UI_CONTEXT_HINT = Pattern.compile(".*on\\s+(page|frame)\\b.*", Pattern.CASE_INSENSITIVE);

    public PageFrameContextGuardResult validate(
            AiGenerationStructuredResponse structuredResponse,
            String defaultUiContext,
            List<String> frameAllowedPages,
            List<String> frameAllowedLocators
    ) {
        Set<String> allowedPages = normalizeSet(frameAllowedPages);
        Set<String> allowedLocators = normalizeSet(frameAllowedLocators);
        String featureFile = structuredResponse != null ? structuredResponse.featureFile() : "";

        List<String> invalidFrameSteps = new ArrayList<>();
        List<String> invalidPageSteps = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<String> blockingErrors = new ArrayList<>();
        int frameCount = 0;
        int pageCount = 0;

        for (String rawLine : featureFile.split("\\R")) {
            String line = rawLine.trim();
            if (line.isBlank()) {
                continue;
            }
            Matcher matcher = PAGE_FRAME_STEP.matcher(line);
            if (!matcher.matches()) {
                if (UI_CONTEXT_HINT.matcher(line).matches() && line.toLowerCase(Locale.ROOT).contains(" on frame ")) {
                    warnings.add("Could not parse frame step context cleanly: " + line);
                }
                continue;
            }

            String context = matcher.group(1).toLowerCase(Locale.ROOT);
            String page = matcher.group(2).toLowerCase(Locale.ROOT);
            String locator = matcher.group(3).toLowerCase(Locale.ROOT);

            if ("frame".equals(context)) {
                frameCount++;
                String compound = page + "." + locator;
                boolean allowed = allowedPages.contains(page) || allowedLocators.contains(compound);
                if (!allowed) {
                    invalidFrameSteps.add(line);
                    blockingErrors.add("Frame step is not allowed for page '" + page + "' locator '" + locator + "'. Use page step instead.");
                }
            } else {
                pageCount++;
                if (!"page".equals(defaultUiContext)) {
                    invalidPageSteps.add(line);
                    warnings.add("Encountered page step while defaultUiContext is '" + defaultUiContext + "': " + line);
                }
            }
        }

        boolean passed = blockingErrors.isEmpty();
        return new PageFrameContextGuardResult(
                passed,
                List.copyOf(invalidFrameSteps),
                List.copyOf(invalidPageSteps),
                List.copyOf(warnings),
                List.copyOf(blockingErrors),
                frameCount,
                pageCount
        );
    }

    private Set<String> normalizeSet(List<String> values) {
        if (values == null || values.isEmpty()) {
            return Set.of();
        }
        Set<String> out = new LinkedHashSet<>();
        for (String value : values) {
            if (value == null || value.isBlank()) {
                continue;
            }
            out.add(value.trim().toLowerCase(Locale.ROOT));
        }
        return out;
    }
}

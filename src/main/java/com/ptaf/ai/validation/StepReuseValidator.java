package com.ptaf.ai.validation;

import com.ptaf.ai.index.StepDefinitionIndex;
import com.ptaf.ai.model.AiGenerationStructuredResponse;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class StepReuseValidator {
    private static final Pattern STEP_LINE = Pattern.compile("^(Given|When|Then|And|But)\\b.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern STEP_PREFIX = Pattern.compile("^(Given|When|Then|And|But)\\s+", Pattern.CASE_INSENSITIVE);
    private static final double LOW_REUSE_THRESHOLD = 50.0;

    public StepReuseValidationResult validate(
            AiGenerationStructuredResponse structuredResponse,
            StepDefinitionIndex stepDefinitionIndex
    ) {
        List<String> featureSteps = extractFeatureSteps(structuredResponse.featureFile());
        List<String> matched = new ArrayList<>();
        List<String> unmatched = new ArrayList<>();

        for (String featureStep : featureSteps) {
            if (matchesAnyKnownStep(featureStep, stepDefinitionIndex.knownSteps())) {
                matched.add(featureStep);
            } else {
                unmatched.add(featureStep);
            }
        }

        List<String> claimedReusedButNotFound = new ArrayList<>();
        for (String claimed : structuredResponse.reusedSteps()) {
            if (!matchesAnyKnownStep(claimed, stepDefinitionIndex.knownSteps())) {
                claimedReusedButNotFound.add(claimed);
            }
        }

        List<String> claimedNewButAlreadyExists = new ArrayList<>();
        for (String claimedNew : structuredResponse.newStepsNeeded()) {
            if (matchesAnyKnownStep(claimedNew, stepDefinitionIndex.knownSteps())) {
                claimedNewButAlreadyExists.add(claimedNew);
            }
        }

        Set<String> normalizedClaimedNew = new LinkedHashSet<>();
        for (String step : structuredResponse.newStepsNeeded()) {
            normalizedClaimedNew.add(normalizeStep(step));
        }

        boolean allUnmatchedClaimedAsNew = true;
        for (String unmatchedStep : unmatched) {
            if (!normalizedClaimedNew.contains(normalizeStep(unmatchedStep))) {
                allUnmatchedClaimedAsNew = false;
                break;
            }
        }

        int total = featureSteps.size();
        int matchedCount = matched.size();
        int unmatchedCount = unmatched.size();
        double reusePercentage = total == 0 ? 0.0 : (matchedCount * 100.0) / total;
        boolean passed = allUnmatchedClaimedAsNew;

        List<String> warnings = new ArrayList<>();
        if (!claimedReusedButNotFound.isEmpty()) {
            for (String step : claimedReusedButNotFound) {
                warnings.add("AI claimed reused step not found: " + step);
            }
        }
        if (!claimedNewButAlreadyExists.isEmpty()) {
            for (String step : claimedNewButAlreadyExists) {
                warnings.add("AI claimed new step already exists: " + step);
            }
        }
        if (total > 0 && reusePercentage < LOW_REUSE_THRESHOLD) {
            warnings.add("Low step reuse percentage: " + String.format(Locale.ROOT, "%.1f", reusePercentage) + "%");
        }

        return new StepReuseValidationResult(
                featureSteps,
                matched,
                unmatched,
                claimedReusedButNotFound,
                claimedNewButAlreadyExists,
                total,
                matchedCount,
                unmatchedCount,
                reusePercentage,
                passed,
                warnings
        );
    }

    private static List<String> extractFeatureSteps(String featureFile) {
        List<String> steps = new ArrayList<>();
        if (featureFile == null || featureFile.isBlank()) {
            return steps;
        }
        for (String line : featureFile.split("\\R")) {
            String trimmed = line.trim();
            if (STEP_LINE.matcher(trimmed).matches()) {
                steps.add(trimmed);
            }
        }
        return steps;
    }

    private static boolean matchesAnyKnownStep(String candidateStep, List<String> knownSteps) {
        String normalizedCandidate = normalizeStep(candidateStep);
        String candidateNoKeywordRaw = removeKeywordAndTrim(candidateStep);
        String candidateNoKeywordCollapsed = collapseSpaces(candidateNoKeywordRaw);

        for (String knownStep : knownSteps) {
            String normalizedKnown = normalizeStep(knownStep);
            if (normalizedKnown.equals(normalizedCandidate)) {
                return true;
            }
            if (matchesParameterizedPattern(normalizedKnown, normalizedCandidate)) {
                return true;
            }
            if (matchesRawRegexPattern(removeKeywordAndTrim(knownStep), candidateNoKeywordCollapsed)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesParameterizedPattern(String knownPattern, String normalizedCandidate) {
        String regex = buildParameterizedRegex(knownPattern);
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(normalizedCandidate).matches();
    }

    private static String buildParameterizedRegex(String knownPattern) {
        StringBuilder regex = new StringBuilder("^");
        for (int i = 0; i < knownPattern.length(); ) {
            if (knownPattern.startsWith("{string}", i)) {
                regex.append("(\"[^\"]*\"|'[^']*'|\\S+)");
                i += "{string}".length();
            } else if (knownPattern.startsWith("{int}", i)) {
                regex.append("-?\\d+");
                i += "{int}".length();
            } else if (knownPattern.startsWith("{double}", i)) {
                regex.append("-?\\d+(?:\\.\\d+)?");
                i += "{double}".length();
            } else {
                char c = knownPattern.charAt(i);
                if ("\\.^$|?*+()[]{}".indexOf(c) >= 0) {
                    regex.append('\\');
                }
                regex.append(c);
                i++;
            }
        }
        regex.append("$");
        return regex.toString();
    }

    private static boolean matchesRawRegexPattern(String knownPattern, String candidate) {
        if (knownPattern == null || knownPattern.isBlank()) {
            return false;
        }
        String regex = stripRegexAnchors(collapseSpaces(knownPattern));
        try {
            return Pattern.compile("^" + regex + "$", Pattern.CASE_INSENSITIVE).matcher(candidate).matches();
        } catch (PatternSyntaxException ignored) {
            return false;
        }
    }

    private static String normalizeStep(String step) {
        String withoutKeyword = removeKeywordAndTrim(step);
        String withoutAnchors = stripRegexAnchors(withoutKeyword);
        return collapseSpaces(withoutAnchors).toLowerCase(Locale.ROOT);
    }

    private static String removeKeywordAndTrim(String step) {
        if (step == null) {
            return "";
        }
        return STEP_PREFIX.matcher(step.trim()).replaceFirst("").trim();
    }

    private static String stripRegexAnchors(String value) {
        if (value == null) {
            return "";
        }
        String out = value;
        if (out.startsWith("^")) {
            out = out.substring(1).trim();
        }
        if (out.endsWith("$")) {
            out = out.substring(0, out.length() - 1).trim();
        }
        return out;
    }

    private static String collapseSpaces(String input) {
        return input == null ? "" : input.trim().replaceAll("\\s+", " ");
    }
}

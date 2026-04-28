package com.ptaf.ai.context;

import com.ptaf.ai.config.AiAssistantProperties;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Ranks existing feature snippets by simple keyword overlap with the requirement.
 */
public final class SimilarFeatureRetriever {
    private static final Pattern NON_WORD = Pattern.compile("[^a-z0-9\\s]");
    private static final Set<String> STOP_WORDS = Set.of(
            "the", "a", "an", "to", "of", "in", "on", "for", "with",
            "and", "or", "is", "are", "be", "should", "can", "user"
    );

    private final int maxSimilarFeatures;
    private final int minSimilarityScore;

    public SimilarFeatureRetriever(AiAssistantProperties properties) {
        this(properties.contextMaxSimilarFeatures(), properties.contextMinSimilarityScore());
    }

    public SimilarFeatureRetriever(int maxSimilarFeatures, int minSimilarityScore) {
        this.maxSimilarFeatures = Math.max(0, maxSimilarFeatures);
        this.minSimilarityScore = Math.max(0, minSimilarityScore);
    }

    public List<String> retrieve(String requirement, FrameworkGenerationContext context) {
        if (requirement == null || requirement.isBlank()) {
            return List.of();
        }
        if (context == null || context.existingFeatureSnippets() == null || context.existingFeatureSnippets().isEmpty()) {
            return List.of();
        }

        Set<String> reqTokens = tokenize(requirement);
        if (reqTokens.isEmpty()) {
            return List.of();
        }

        List<ScoredSnippet> ranked = new ArrayList<>();
        for (String snippet : context.existingFeatureSnippets()) {
            if (snippet == null || snippet.isBlank()) {
                continue;
            }
            Set<String> snippetTokens = tokenize(snippet);
            int score = overlapScore(reqTokens, snippetTokens);
            if (score >= minSimilarityScore) {
                ranked.add(new ScoredSnippet(snippet, score));
            }
        }

        ranked.sort(
                Comparator.comparingInt(ScoredSnippet::score).reversed()
                        .thenComparingInt(s -> s.snippet().length())
        );

        List<String> out = new ArrayList<>();
        for (ScoredSnippet s : ranked) {
            if (out.size() >= maxSimilarFeatures) {
                break;
            }
            out.add(s.snippet());
        }
        return out;
    }

    private static int overlapScore(Set<String> reqTokens, Set<String> snippetTokens) {
        int score = 0;
        for (String token : reqTokens) {
            if (snippetTokens.contains(token)) {
                score++;
            }
        }
        return score;
    }

    private static Set<String> tokenize(String text) {
        String normalized = NON_WORD.matcher(text.toLowerCase(Locale.ROOT)).replaceAll(" ");
        String[] parts = normalized.trim().split("\\s+");
        Set<String> tokens = new LinkedHashSet<>();
        for (String token : parts) {
            if (token.isBlank() || STOP_WORDS.contains(token)) {
                continue;
            }
            tokens.add(token);
        }
        return tokens;
    }

    private record ScoredSnippet(String snippet, int score) {
    }
}

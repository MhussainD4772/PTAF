package com.ptaf.ai.rank;

import com.ptaf.ai.model.ScoredPattern;
import com.ptaf.ai.model.SourceChunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Phase 2: lightweight retrieval — token overlap with the requirement (no embeddings).
 */
public final class KeywordOverlapRanker {

    private static final Pattern NON_WORD = Pattern.compile("[^a-z0-9]+");
    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "and", "or", "but", "if", "on", "in", "at", "to", "for", "of", "with", "as",
            "is", "are", "be", "by", "from", "that", "this", "it", "we", "you", "can", "should", "must"
    ));

    private KeywordOverlapRanker() {
    }

    public static List<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String lower = text.toLowerCase(Locale.ROOT);
        String[] parts = NON_WORD.split(lower);
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            if (p.length() > 1 && !STOPWORDS.contains(p)) {
                out.add(p);
            }
        }
        return out;
    }

    public static double scoreOverlap(List<String> reqTokens, String haystack) {
        if (haystack == null || haystack.isEmpty() || reqTokens.isEmpty()) {
            return 0;
        }
        String h = haystack.toLowerCase(Locale.ROOT);
        double score = 0;
        for (String t : reqTokens) {
            if (h.contains(t)) {
                score += 1;
            }
        }
        return score;
    }

    public static double scoreChunk(List<String> reqTokens, SourceChunk chunk) {
        double s = scoreOverlap(reqTokens, chunk.content());
        s += 0.5 * scoreOverlap(reqTokens, chunk.relativePath().replace('/', ' '));
        return s;
    }

    public static List<SourceChunk> rankChunks(List<String> reqTokens, List<SourceChunk> chunks, int topK) {
        record Row(SourceChunk c, double s) {
        }
        List<Row> rows = new ArrayList<>();
        for (SourceChunk c : chunks) {
            rows.add(new Row(c, scoreChunk(reqTokens, c)));
        }
        rows.sort(Comparator.comparingDouble((Row r) -> r.s).reversed().thenComparing(r -> r.c.relativePath()));
        List<SourceChunk> out = new ArrayList<>();
        for (int i = 0; i < rows.size() && out.size() < topK; i++) {
            if (rows.get(i).s > 0) {
                out.add(rows.get(i).c);
            }
        }
        if (out.isEmpty()) {
            for (int i = 0; i < Math.min(topK, rows.size()); i++) {
                out.add(rows.get(i).c);
            }
        }
        return out;
    }

    public static List<ScoredPattern> rankPatterns(
            List<String> reqTokens,
            List<ScoredPattern> candidates,
            int topK
    ) {
        List<ScoredPattern> scored = new ArrayList<>();
        for (ScoredPattern c : candidates) {
            double s = scoreOverlap(reqTokens, c.pattern());
            s += 0.3 * scoreOverlap(reqTokens, c.sourceRelativePath());
            scored.add(new ScoredPattern(c.pattern(), c.sourceRelativePath(), s));
        }
        scored.sort(Comparator.comparingDouble(ScoredPattern::score).reversed());
        return scored.subList(0, Math.min(topK, scored.size()));
    }

    public static String buildSection(String title, List<SourceChunk> ranked, int maxChars) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- ").append(title).append(" (ranked by keyword overlap) ---\n");
        int used = 0;
        for (SourceChunk c : ranked) {
            String header = "[" + c.kind() + "] " + c.relativePath() + "\n";
            String body = c.content();
            int next = used + header.length() + body.length() + 2;
            if (next > maxChars) {
                int room = maxChars - used - header.length();
                if (room > 200) {
                    body = body.substring(0, Math.min(body.length(), room)) + "\n[TRUNCATED]\n";
                } else {
                    break;
                }
            }
            sb.append(header).append(body).append("\n\n");
            used = sb.length();
            if (used >= maxChars) {
                break;
            }
        }
        return sb.toString();
    }
}

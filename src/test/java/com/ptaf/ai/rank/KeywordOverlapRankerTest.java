package com.ptaf.ai.rank;

import com.ptaf.ai.model.ScoredPattern;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeywordOverlapRankerTest {

    @Test
    void ranksPatternsByOverlap() {
        var req = KeywordOverlapRanker.tokenize("user clicks login button");
        List<ScoredPattern> c = List.of(
                new ScoredPattern("^we navigate$", "A.java", 0),
                new ScoredPattern("^we click on page(.*)login$", "B.java", 0)
        );
        List<ScoredPattern> ranked = KeywordOverlapRanker.rankPatterns(req, c, 10);
        assertFalse(ranked.isEmpty());
        assertTrue(ranked.get(0).pattern().toLowerCase().contains("click"));
    }
}

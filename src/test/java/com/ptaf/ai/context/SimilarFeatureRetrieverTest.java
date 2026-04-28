package com.ptaf.ai.context;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimilarFeatureRetrieverTest {

    @Test
    void exactKeywordMatchReturnsRelevantSnippet() {
        SimilarFeatureRetriever retriever = new SimilarFeatureRetriever(3, 1);
        FrameworkGenerationContext context = contextWithSnippets(
                "Feature: Login\nScenario: valid login\nGiven user enters username and password",
                "Feature: Transfer\nScenario: transfer funds"
        );

        List<String> result = retriever.retrieve("User should login successfully", context);
        assertEquals(1, result.size());
        assertTrue(result.get(0).toLowerCase().contains("login"));
    }

    @Test
    void irrelevantSnippetIsExcludedByMinScore() {
        SimilarFeatureRetriever retriever = new SimilarFeatureRetriever(3, 2);
        FrameworkGenerationContext context = contextWithSnippets(
                "Feature: Transfer\nScenario: transfer funds",
                "Feature: Statements\nScenario: download pdf"
        );

        List<String> result = retriever.retrieve("Login with password", context);
        assertTrue(result.isEmpty());
    }

    @Test
    void topNLimitIsRespected() {
        SimilarFeatureRetriever retriever = new SimilarFeatureRetriever(2, 1);
        FrameworkGenerationContext context = contextWithSnippets(
                "Feature: Login alpha\nScenario: login",
                "Feature: Login beta\nScenario: login",
                "Feature: Login gamma\nScenario: login"
        );

        List<String> result = retriever.retrieve("login", context);
        assertEquals(2, result.size());
    }

    @Test
    void stopWordsAreIgnored() {
        SimilarFeatureRetriever retriever = new SimilarFeatureRetriever(3, 1);
        FrameworkGenerationContext context = contextWithSnippets(
                "Feature: Payments\nScenario: bill pay",
                "Feature: Login\nScenario: user login"
        );

        List<String> result = retriever.retrieve("the user should be able to login", context);
        assertEquals(1, result.size());
        assertTrue(result.get(0).toLowerCase().contains("login"));
    }

    @Test
    void higherOverlapRanksFirst() {
        SimilarFeatureRetriever retriever = new SimilarFeatureRetriever(3, 1);
        String high = "Feature: Login and dashboard\nScenario: login dashboard";
        String low = "Feature: Login only\nScenario: login";
        FrameworkGenerationContext context = contextWithSnippets(low, high);

        List<String> result = retriever.retrieve("login dashboard", context);
        assertEquals(high, result.get(0));
    }

    @Test
    void tieBreakerPrefersShorterSnippet() {
        SimilarFeatureRetriever retriever = new SimilarFeatureRetriever(3, 1);
        String shortSnippet = "Feature: Login\nScenario: login";
        String longSnippet = "Feature: Login\nScenario: login\nGiven login\nWhen login\nThen login";
        FrameworkGenerationContext context = contextWithSnippets(longSnippet, shortSnippet);

        List<String> result = retriever.retrieve("login", context);
        assertEquals(shortSnippet, result.get(0));
    }

    @Test
    void emptyRequirementReturnsEmptyList() {
        SimilarFeatureRetriever retriever = new SimilarFeatureRetriever(3, 1);
        FrameworkGenerationContext context = contextWithSnippets("Feature: Login");

        assertTrue(retriever.retrieve("", context).isEmpty());
        assertTrue(retriever.retrieve("   ", context).isEmpty());
    }

    @Test
    void emptyContextSnippetsReturnsEmptyList() {
        SimilarFeatureRetriever retriever = new SimilarFeatureRetriever(3, 1);
        FrameworkGenerationContext context = new FrameworkGenerationContext(
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
        assertTrue(retriever.retrieve("login", context).isEmpty());
    }

    private static FrameworkGenerationContext contextWithSnippets(String... snippets) {
        return new FrameworkGenerationContext(
                List.of(snippets),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }
}

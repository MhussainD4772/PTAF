package com.ptaf.ai.parse;

import com.ptaf.ai.model.GenerationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiResponseParserTest {

    @Test
    void parsesDelimitedSections() {
        String raw = """
                <<<FEATURE_GHERKIN>>>
                Feature: Sample
                  Scenario: One
                    Given something
                <<<END_FEATURE_GHERKIN>>>
                <<<SUGGESTED_REUSABLE_STEPS>>>
                - reuse this step
                - and this one
                <<<END_SUGGESTED_REUSABLE_STEPS>>>
                """;
        GenerationResult r = AiResponseParser.parse(raw);
        assertTrue(r.featureGherkin().contains("Feature: Sample"));
        assertEquals(2, r.suggestedReusableSteps().size());
        assertEquals("reuse this step", r.suggestedReusableSteps().get(0));
    }
}

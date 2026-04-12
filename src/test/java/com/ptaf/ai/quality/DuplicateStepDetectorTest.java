package com.ptaf.ai.quality;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DuplicateStepDetectorTest {

    @Test
    void detectsRepeatedSteps() {
        List<String> lines = List.of(
                "Feature: X",
                "  Scenario: A",
                "    Given login works",
                "  Scenario: B",
                "    Given login works"
        );
        var occ = DuplicateStepDetector.scanFile("f.feature", lines);
        var dups = DuplicateStepDetector.findDuplicates(occ, 2);
        assertFalse(dups.isEmpty());
        assertEquals("login works", dups.get(0).normalizedStep());
        assertEquals(2, dups.get(0).count());
    }
}

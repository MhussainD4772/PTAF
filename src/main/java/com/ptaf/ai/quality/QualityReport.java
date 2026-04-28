package com.ptaf.ai.quality;

import java.util.List;

/** Phase 3 aggregate result for JSON/HTML reports and exit codes. */
public record QualityReport(
        List<String> syntaxIssues,
        List<DuplicateStepDetector.DuplicateGroup> duplicateGroups,
        int warnThreshold,
        boolean failedStrict
) {
    public boolean hasErrors() {
        return failedStrict || !syntaxIssues.isEmpty();
    }
}

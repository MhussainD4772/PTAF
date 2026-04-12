package com.ptaf.ai.quality;

import com.ptaf.ai.policy.AiPolicy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Phase 3: scan all {@code .feature} files under a directory, validate structure, detect duplicate steps.
 */
public final class QualityGateService {

    private final AiPolicy policy;

    public QualityGateService() {
        this(new AiPolicy());
    }

    public QualityGateService(AiPolicy policy) {
        this.policy = policy;
    }

    public QualityReport run(Path projectRoot, String featuresRelativeDir, boolean strict) throws IOException {
        Path root = projectRoot.resolve(featuresRelativeDir).normalize();
        List<String> syntaxIssues = new ArrayList<>();
        List<StepOccurrence> allSteps = new ArrayList<>();

        if (!Files.isDirectory(root)) {
            syntaxIssues.add("Features directory not found: " + featuresRelativeDir);
            boolean fail = strict && !syntaxIssues.isEmpty();
            return new QualityReport(syntaxIssues, List.of(), policy.duplicateStepWarnThreshold(), fail);
        }

        List<Path> features;
        try (Stream<Path> walk = Files.walk(root)) {
            features = walk
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".feature"))
                    .sorted(Comparator.comparing(Path::toString))
                    .collect(Collectors.toList());
        }

        for (Path p : features) {
            String rel = projectRoot.relativize(p).toString().replace('\\', '/');
            String content = Files.readString(p, StandardCharsets.UTF_8);
            syntaxIssues.addAll(BasicGherkinValidator.validate(rel, content));
            List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);
            allSteps.addAll(DuplicateStepDetector.scanFile(rel, lines));
        }

        int warn = policy.duplicateStepWarnThreshold();
        int failTh = policy.duplicateStepFailThreshold();
        List<DuplicateStepDetector.DuplicateGroup> dups = DuplicateStepDetector.findDuplicates(allSteps, warn);

        boolean shouldFail = strict && (!syntaxIssues.isEmpty()
                || dups.stream().anyMatch(g -> g.count() >= failTh));
        return new QualityReport(syntaxIssues, dups, warn, shouldFail);
    }
}

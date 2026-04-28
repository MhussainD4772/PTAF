package com.ptaf.ai.index;

import com.ptaf.ai.StepPatternExtractor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Index of known step definition patterns extracted from Java annotation values.
 */
public final class StepDefinitionIndex {
    private final List<String> knownSteps;

    public StepDefinitionIndex(List<String> knownSteps) {
        this.knownSteps = List.copyOf(knownSteps);
    }

    public List<String> knownSteps() {
        return knownSteps;
    }

    public static StepDefinitionIndex build(Path projectRoot, List<String> stepDefinitionPaths) throws IOException {
        Set<String> ordered = new LinkedHashSet<>();
        for (String relativePath : stepDefinitionPaths) {
            if (relativePath == null || relativePath.isBlank()) {
                continue;
            }
            Path root = projectRoot.resolve(relativePath).normalize();
            if (!Files.isDirectory(root)) {
                continue;
            }
            for (Path javaFile : listJavaFiles(root)) {
                String source = Files.readString(javaFile, StandardCharsets.UTF_8);
                ordered.addAll(StepPatternExtractor.fromJavaSource(source));
            }
        }
        return new StepDefinitionIndex(new ArrayList<>(ordered));
    }

    private static List<Path> listJavaFiles(Path root) throws IOException {
        try (Stream<Path> walk = Files.walk(root)) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(".java"))
                    .sorted(Comparator.comparing(Path::toString))
                    .collect(Collectors.toList());
        }
    }
}

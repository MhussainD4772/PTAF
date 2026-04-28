package com.ptaf.ai.context;

import com.ptaf.ai.config.AiAssistantProperties;
import com.ptaf.ai.index.StepDefinitionIndex;
import com.ptaf.ai.index.YamlKeyIndex;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Collects reusable framework context before AI generation.
 */
public final class FrameworkContextCollector {
    private final AiAssistantProperties properties;

    public FrameworkContextCollector(AiAssistantProperties properties) {
        this.properties = properties;
    }

    public FrameworkGenerationContext collect(Path projectRoot) throws IOException {
        StepDefinitionIndex stepIndex = StepDefinitionIndex.build(projectRoot, properties.contextStepDefinitionPaths());
        YamlKeyIndex yamlIndex = YamlKeyIndex.build(projectRoot, properties.contextYamlPaths());

        List<String> featureSnippets = collectFeatureSnippets(projectRoot);
        List<String> stepDefinitions = stepIndex.knownSteps();
        List<String> yamlKeys = yamlIndex.normalizedKeys().stream().sorted().toList();

        List<String> uiKeys = yamlKeys.stream().filter(k -> k.startsWith("elements.")).toList();
        List<String> apiKeys = yamlKeys.stream().filter(k -> k.startsWith("api_requests.")).toList();
        List<String> dbKeys = yamlKeys.stream().filter(k -> k.startsWith("queries.")).toList();

        return new FrameworkGenerationContext(
                featureSnippets,
                stepDefinitions,
                yamlKeys,
                uiKeys,
                apiKeys,
                dbKeys
        );
    }

    private List<String> collectFeatureSnippets(Path projectRoot) throws IOException {
        List<Path> featureFiles = new ArrayList<>();
        for (String relativePath : properties.contextFeaturePaths()) {
            if (relativePath == null || relativePath.isBlank()) {
                continue;
            }
            Path root = projectRoot.resolve(relativePath).normalize();
            if (!Files.isDirectory(root)) {
                continue;
            }
            try (Stream<Path> walk = Files.walk(root)) {
                featureFiles.addAll(
                        walk.filter(Files::isRegularFile)
                                .filter(path -> path.toString().toLowerCase(Locale.ROOT).endsWith(".feature"))
                                .sorted(Comparator.comparing(Path::toString))
                                .collect(Collectors.toList())
                );
            }
        }

        int limit = Math.max(0, properties.contextMaxFeatureSnippets());
        List<String> snippets = new ArrayList<>();
        for (Path file : featureFiles) {
            if (snippets.size() >= limit) {
                break;
            }
            String content = Files.readString(file, StandardCharsets.UTF_8);
            String snippet = buildFeatureSnippet(content);
            if (!snippet.isBlank()) {
                snippets.add(snippet);
            }
        }
        return snippets;
    }

    private static String buildFeatureSnippet(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }
        List<String> lines = new ArrayList<>();
        for (String rawLine : content.split("\\R")) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String lower = line.toLowerCase(Locale.ROOT);
            if (lower.startsWith("feature:")
                    || lower.startsWith("scenario:")
                    || lower.startsWith("scenario outline:")
                    || lower.startsWith("given ")
                    || lower.startsWith("when ")
                    || lower.startsWith("then ")
                    || lower.startsWith("and ")
                    || lower.startsWith("but ")
                    || lower.startsWith("@")) {
                lines.add(line);
            }
            if (lines.size() >= 12) {
                break;
            }
        }
        return String.join("\n", lines);
    }
}

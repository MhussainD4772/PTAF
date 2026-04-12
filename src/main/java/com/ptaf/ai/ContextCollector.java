package com.ptaf.ai;

import com.ptaf.ai.config.AiAssistantProperties;
import com.ptaf.ai.model.ProjectContext;
import com.ptaf.ai.model.ScoredPattern;
import com.ptaf.ai.model.SourceChunk;
import com.ptaf.ai.rank.KeywordOverlapRanker;

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
 * Phase 2: loads features, step defs, hooks, pages, element YAML, config YAML — then ranks by keyword overlap
 * with the requirement before building prompt sections.
 */
public final class ContextCollector {

    private final AiAssistantProperties properties;

    public ContextCollector(AiAssistantProperties properties) {
        this.properties = properties;
    }

    public ProjectContext collect(Path projectRoot, String requirement) throws IOException {
        List<String> reqTokens = KeywordOverlapRanker.tokenize(requirement);
        int budget = properties.maxTotalContextChars();
        int bf = (int) (budget * 0.22);
        int bs = (int) (budget * 0.22);
        int bfw = (int) (budget * 0.40);

        List<SourceChunk> featureChunks = loadFileChunks(
                projectRoot, properties.featuresDir(), "FEATURE", "feature", properties.maxFeatureFiles());
        List<SourceChunk> rankedFeatures = KeywordOverlapRanker.rankChunks(
                reqTokens, featureChunks, properties.rankingTopChunks());
        String featuresSection = KeywordOverlapRanker.buildSection("FEATURES", rankedFeatures, bf);

        List<SourceChunk> stepChunks = loadFileChunks(
                projectRoot, properties.stepDefinitionsDir(), "STEP_DEF", "java", properties.maxStepDefFiles());
        List<SourceChunk> rankedSteps = KeywordOverlapRanker.rankChunks(
                reqTokens, stepChunks, properties.rankingTopChunks());
        String stepSection = KeywordOverlapRanker.buildSection("STEP_DEFINITIONS", rankedSteps, bs);

        List<ScoredPattern> patternCandidates = new ArrayList<>();
        for (SourceChunk ch : stepChunks) {
            for (String p : StepPatternExtractor.fromJavaSource(ch.content())) {
                patternCandidates.add(new ScoredPattern(p, ch.relativePath(), 0));
            }
        }
        List<ScoredPattern> rankedPatterns = KeywordOverlapRanker.rankPatterns(
                reqTokens, patternCandidates, properties.rankingTopPatterns());
        List<String> patternStrings = rankedPatterns.stream().map(ScoredPattern::pattern).toList();

        List<SourceChunk> frameworkChunks = new ArrayList<>();
        frameworkChunks.addAll(loadFileChunks(
                projectRoot, properties.hooksDir(), "HOOK", "java", properties.maxHooksFiles()));
        frameworkChunks.addAll(loadFileChunks(
                projectRoot, properties.uiPagesDir(), "PAGE", "java", properties.maxPagesFiles()));
        frameworkChunks.addAll(loadYamlChunks(
                projectRoot, properties.elementsDir(), "ELEMENT_YAML", properties.maxElementsFiles()));
        frameworkChunks.addAll(loadYamlChunks(
                projectRoot, properties.configYamlDir(), "CONFIG_YAML", properties.maxConfigYamlFiles()));

        List<SourceChunk> rankedFw = KeywordOverlapRanker.rankChunks(
                reqTokens, frameworkChunks, properties.rankingTopChunks());
        String frameworkSection = KeywordOverlapRanker.buildSection("FRAMEWORK_CONTEXT", rankedFw, bfw);

        return new ProjectContext(featuresSection, stepSection, frameworkSection, patternStrings, rankedPatterns);
    }

    private static List<SourceChunk> loadFileChunks(
            Path projectRoot,
            String relativeDir,
            String kind,
            String extension,
            int limit
    ) throws IOException {
        Path root = projectRoot.resolve(relativeDir).normalize();
        List<Path> files = listLimited(root, extension, limit);
        List<SourceChunk> out = new ArrayList<>();
        for (Path p : files) {
            String rel = projectRoot.relativize(p).toString().replace('\\', '/');
            String content = Files.readString(p, StandardCharsets.UTF_8);
            out.add(new SourceChunk(rel, kind, content));
        }
        return out;
    }

    private static List<SourceChunk> loadYamlChunks(
            Path projectRoot,
            String relativeDir,
            String kind,
            int limit
    ) throws IOException {
        Path root = projectRoot.resolve(relativeDir).normalize();
        if (!Files.isDirectory(root)) {
            return List.of();
        }
        List<Path> ymlFiles;
        try (Stream<Path> walk = Files.walk(root)) {
            ymlFiles = walk
                    .filter(Files::isRegularFile)
                    .filter(p -> {
                        String n = p.toString().toLowerCase();
                        return n.endsWith(".yml") || n.endsWith(".yaml");
                    })
                    .sorted(Comparator.comparing(Path::toString))
                    .limit(Math.max(0, limit))
                    .collect(Collectors.toList());
        }
        List<SourceChunk> out = new ArrayList<>();
        for (Path p : ymlFiles) {
            String rel = projectRoot.relativize(p).toString().replace('\\', '/');
            String content = Files.readString(p, StandardCharsets.UTF_8);
            out.add(new SourceChunk(rel, kind, content));
        }
        return out;
    }

    private static List<Path> listLimited(Path root, String extension, int limit) throws IOException {
        if (!Files.isDirectory(root)) {
            return List.of();
        }
        try (Stream<Path> walk = Files.walk(root)) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith("." + extension))
                    .sorted(Comparator.comparing(Path::toString))
                    .limit(Math.max(0, limit))
                    .collect(Collectors.toList());
        }
    }
}

package com.ptaf.ai;

import com.ptaf.ai.config.AiAssistantProperties;
import com.ptaf.ai.context.FrameworkContextCollector;
import com.ptaf.ai.context.SimilarFeatureRetriever;
import com.ptaf.ai.index.StepDefinitionIndex;
import com.ptaf.ai.index.YamlKeyIndex;
import com.ptaf.ai.model.GenerationResult;
import com.ptaf.ai.parser.StructuredAiResponseParser;
import com.ptaf.ai.audit.AuditLog;
import com.ptaf.ai.policy.AiPolicy;
import com.ptaf.ai.validation.StepReuseValidator;
import com.ptaf.ai.validation.AllowedYamlGuard;
import com.ptaf.ai.validation.MissingYamlPatchSuggester;
import com.ptaf.ai.validation.RunnableFeatureGate;
import com.ptaf.ai.validation.YamlKeyValidator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Wires context → prompts → Gemini → parse → optional file write. */
public final class FeatureGeneratorService {

    private final AiAssistantProperties properties;
    private final AiModelClient modelClient;
    private final FrameworkContextCollector frameworkContextCollector;
    private final SimilarFeatureRetriever similarFeatureRetriever;
    private final PromptBuilder promptBuilder;
    private final AiPolicy policy;

    public FeatureGeneratorService(AiAssistantProperties properties) {
        this(properties, new AiPolicy(), new GeminiModelClient());
    }

    public FeatureGeneratorService(AiAssistantProperties properties, AiPolicy policy) {
        this(properties, policy, new GeminiModelClient());
    }

    public FeatureGeneratorService(AiAssistantProperties properties, AiPolicy policy, AiModelClient modelClient) {
        this.properties = properties;
        this.policy = policy;
        this.modelClient = modelClient;
        this.frameworkContextCollector = new FrameworkContextCollector(properties);
        this.similarFeatureRetriever = new SimilarFeatureRetriever(properties);
        this.promptBuilder = new PromptBuilder(properties);
    }

    public GenerationResult generate(Path projectRoot, String requirement) throws Exception {
        String blocked = policy.validateRequirement(requirement);
        if (blocked != null) {
            throw new IllegalStateException("Policy rejected requirement: " + blocked);
        }
        var frameworkContext = frameworkContextCollector.collect(projectRoot);
        var similar = similarFeatureRetriever.retrieve(requirement, frameworkContext);
        String system = promptBuilder.systemPrompt();
        String user = promptBuilder.userPrompt(requirement, frameworkContext, similar);
        String raw = modelClient.generate(system, user, properties);
        var structured = StructuredAiResponseParser.parse(raw);
        var stepIndex = StepDefinitionIndex.build(projectRoot, properties.stepDefinitionPaths());
        var stepReuseValidation = new StepReuseValidator().validate(structured, stepIndex);
        var yamlIndex = YamlKeyIndex.build(projectRoot, properties.yamlPaths());
        var yamlValidation = new YamlKeyValidator().validate(structured, yamlIndex);
        var allowedYamlGuardResult = new AllowedYamlGuard().validate(structured, yamlIndex);
        var runnableFeatureResult = new RunnableFeatureGate().evaluate(
                structured,
                stepReuseValidation,
                yamlValidation,
                allowedYamlGuardResult
        );
        var missingYamlPatchSuggestions = new MissingYamlPatchSuggester().suggest(
                yamlValidation,
                allowedYamlGuardResult
        );
        var gen = new GenerationResult(
                structured.featureFile(),
                structured.reusedSteps(),
                raw,
                List.of(),
                structured,
                stepReuseValidation,
                yamlValidation,
                allowedYamlGuardResult,
                runnableFeatureResult,
                missingYamlPatchSuggestions
        );
        AuditLog.append("generate", properties.model(), "success", AuditLog.sha256Prefix(requirement, 16));
        return gen;
    }

    public Path writeFeatureFile(Path outputFile, GenerationResult result, boolean overwrite) throws IOException {
        if (Files.exists(outputFile) && !overwrite) {
            throw new IllegalStateException("Output file already exists: " + outputFile + " (use --overwrite to replace)");
        }
        Path parent = outputFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        String content = result.featureGherkin().trim() + "\n";
        Files.writeString(outputFile, content, StandardCharsets.UTF_8);
        return outputFile;
    }
}

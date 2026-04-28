package com.ptaf.ai.cli;

import com.ptaf.ai.FeatureGeneratorService;
import com.ptaf.ai.audit.AiGenerationAuditLogger;
import com.ptaf.ai.audit.AiGenerationAuditRecord;
import com.ptaf.ai.config.AiAssistantProperties;
import com.ptaf.ai.http.AiGenerateHttpServer;
import com.ptaf.ai.model.AiGenerationMode;
import com.ptaf.ai.model.GenerationResult;
import com.ptaf.ai.quality.QualityGateService;
import com.ptaf.ai.quality.QualityReportWriter;
import com.ptaf.ai.telemetry.TelemetrySummaryWriter;
import com.ptaf.ai.triage.TriageService;
import com.ptaf.ai.validation.GenerationModeEvaluator;
import com.sun.net.httpserver.HttpServer;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * PTAF AI CLI: generate, serve, quality (Phase 3), triage (Phase 4).
 */
@Command(
        name = "ptaf-ai",
        mixinStandardHelpOptions = true,
        version = "1.0",
        subcommands = {
                AiAssistantCli.GenerateCommand.class,
                AiAssistantCli.ServeCommand.class,
                AiAssistantCli.QualityCommand.class,
                AiAssistantCli.TriageCommand.class,
                AiAssistantCli.TelemetryReportCommand.class
        },
        description = "PTAF AI assistant — Gemini Gherkin + quality gate + triage"
)
public class AiAssistantCli implements Callable<Integer> {

    public static void main(String[] args) {
        int code = new CommandLine(new AiAssistantCli()).execute(args);
        System.exit(code);
    }

    @Override
    public Integer call() {
        CommandLine.usage(this, System.out);
        return 0;
    }

    @Command(
            name = "generate",
            mixinStandardHelpOptions = true,
            description = "Call Gemini and write a .feature draft (Phase 4 policy applies)"
    )
    static final class GenerateCommand implements Callable<Integer> {

        @Option(names = {"-r", "--requirement"}, description = "Requirement / user story")
        String requirement;

        @Option(
                names = {"--requirement-file"},
                description = "UTF-8 file with the requirement (use instead of -r to avoid shell quoting issues)"
        )
        Path requirementFile;

        @Option(names = {"-o", "--output"}, description = "Output path (default: target/ai-proposals/generated.feature)")
        Path output;

        @Option(names = {"--mode"}, description = "Generation mode: preview|write|strict", defaultValue = "preview")
        String mode;

        @Option(names = {"--overwrite"}, description = "Allow overwriting an existing output file in write mode")
        boolean overwrite;

        @Option(names = {"--project-root"}, description = "Repo root (default: user.dir)")
        Path projectRoot = Path.of(System.getProperty("user.dir"));

        @Override
        public Integer call() throws Exception {
            AiAssistantProperties props = new AiAssistantProperties();
            validateGemini(props);
            String req = resolveRequirement();
            Path out = output != null ? output : Path.of("target", "ai-proposals", "generated.feature");
            AiGenerationMode generationMode = AiGenerationMode.fromString(mode);
            FeatureGeneratorService service = new FeatureGeneratorService(props);
            GenerationResult result = null;
            List<String> blockingErrors = new ArrayList<>();
            Path written = null;
            int exitCode = 0;
            try {
                result = service.generate(projectRoot, req);
                GenerationModeEvaluator modeEvaluator = new GenerationModeEvaluator();
                blockingErrors = modeEvaluator.blockingErrors(generationMode, result);
                if (modeEvaluator.shouldWriteFile(generationMode, blockingErrors)) {
                    written = service.writeFeatureFile(out, result, overwrite);
                }
                if (generationMode != AiGenerationMode.PREVIEW && !blockingErrors.isEmpty()) {
                    exitCode = 1;
                }
            } catch (Exception e) {
                blockingErrors = List.of(e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
                exitCode = 1;
            }

            System.out.println("=== Generation Result ===");
            System.out.println("Mode: " + generationMode);
            System.out.println("File written: " + (written != null));
            if (written != null) {
                System.out.println("Path: " + written.toAbsolutePath());
            }
            if (result != null) {
                System.out.println("Parse: " + (result.structuredResponse().parseSuccessful() ? "passed" : "failed"));
                System.out.println("Suggested steps: " + result.suggestedReusableSteps().size());
            }
            if (result != null && result.stepReuseValidationResult() != null) {
                var v = result.stepReuseValidationResult();
                System.out.println("\n=== Step Reuse Validation ===");
                System.out.println("Total steps: " + v.totalSteps());
                System.out.println("Matched existing steps: " + v.matchedCount());
                System.out.println("New steps needed: " + v.unmatchedCount());
                System.out.println("Reuse percentage: " + String.format(Locale.ROOT, "%.1f", v.reusePercentage()) + "%");
                if (!v.warnings().isEmpty()) {
                    System.out.println("Warnings:");
                    for (String warning : v.warnings()) {
                        System.out.println("- " + warning);
                    }
                }
            }
            if (result != null && result.yamlKeyValidationResult() != null) {
                var y = result.yamlKeyValidationResult();
                System.out.println("\n=== YAML Key Validation ===");
                System.out.println("Total keys used: " + y.totalKeys());
                System.out.println("Existing keys: " + y.existingCount());
                System.out.println("Missing keys: " + y.missingCount());
                if (!y.missingKeys().isEmpty()) {
                    System.out.println("Missing:");
                    for (String missing : y.missingKeys()) {
                        System.out.println("- " + missing);
                    }
                    System.out.println("Suggested patch:");
                    for (String missing : y.missingKeys()) {
                        String patch = y.suggestedPatches().get(missing);
                        if (patch != null && !patch.isBlank()) {
                            System.out.println(patch);
                        }
                    }
                }
                if (!y.warnings().isEmpty()) {
                    System.out.println("Warnings:");
                    for (String warning : y.warnings()) {
                        System.out.println("- " + warning);
                    }
                }
                System.out.println("YAML validation: " + y.existingCount() + "/" + y.totalKeys() + " keys found");
            }
            if (result != null && result.runnableFeatureResult() != null) {
                var r = result.runnableFeatureResult();
                System.out.println("\n=== Runnable Feature Gate ===");
                System.out.println("Runnable: " + r.runnable());
                if (!r.blockingReasons().isEmpty()) {
                    System.out.println("Blocking reasons:");
                    for (String reason : r.blockingReasons()) {
                        System.out.println("- " + reason);
                    }
                }
            }
            if (result != null && result.allowedYamlGuardResult() != null) {
                var g = result.allowedYamlGuardResult();
                System.out.println("\n=== Allowed YAML Guard ===");
                System.out.println("Passed: " + g.passed());
                if (!g.unknownKeysUsed().isEmpty()) {
                    System.out.println("Unknown keys used:");
                    for (String key : g.unknownKeysUsed()) {
                        System.out.println("- " + key);
                    }
                }
                if (!g.blockingErrors().isEmpty()) {
                    System.out.println("Blocking errors:");
                    for (String err : g.blockingErrors()) {
                        System.out.println("- " + err);
                    }
                }
                if (!g.warnings().isEmpty()) {
                    System.out.println("Warnings:");
                    for (String warning : g.warnings()) {
                        System.out.println("- " + warning);
                    }
                }
            }
            if (result != null && result.missingYamlPatchSuggestions() != null
                    && !result.missingYamlPatchSuggestions().isEmpty()) {
                System.out.println("\n=== Missing YAML Patch Suggestions ===");
                for (var suggestion : result.missingYamlPatchSuggestions()) {
                    System.out.println("Key: " + suggestion.missingKey());
                    System.out.println("Target: " + suggestion.targetFolder());
                    System.out.println("Patch:");
                    System.out.println(suggestion.suggestedYaml());
                    if (!suggestion.warnings().isEmpty()) {
                        System.out.println("Warnings:");
                        for (String warning : suggestion.warnings()) {
                            System.out.println("- " + warning);
                        }
                    }
                    System.out.println();
                }
            }

            List<String> warnings = collectWarnings(result);
            AiGenerationAuditRecord auditRecord = buildAuditRecord(
                    generationMode,
                    props,
                    req,
                    out,
                    written,
                    result,
                    blockingErrors,
                    warnings
            );
            AiGenerationAuditLogger.WriteResult auditWrite = new AiGenerationAuditLogger()
                    .append(projectRoot, props.auditEnabled(), props.auditOutputPath(), auditRecord);
            if (auditWrite.written()) {
                System.out.println("\n=== Audit ===");
                System.out.println("Audit log written:");
                System.out.println(auditWrite.outputPath());
            } else if (auditWrite.warningMessage() != null) {
                System.out.println(auditWrite.warningMessage());
            }

            if (blockingErrors.isEmpty()) {
                System.out.println("\nBlocking errors: none");
                return exitCode;
            }
            System.out.println("\nBlocking errors:");
            for (String error : blockingErrors) {
                System.out.println("- " + error);
            }
            return exitCode;
        }

        private static List<String> collectWarnings(GenerationResult result) {
            if (result == null) {
                return List.of();
            }
            List<String> warnings = new ArrayList<>();
            warnings.addAll(result.structuredResponse().warnings());
            if (result.stepReuseValidationResult() != null) {
                warnings.addAll(result.stepReuseValidationResult().warnings());
            }
            if (result.yamlKeyValidationResult() != null) {
                warnings.addAll(result.yamlKeyValidationResult().warnings());
            }
            if (result.allowedYamlGuardResult() != null) {
                warnings.addAll(result.allowedYamlGuardResult().warnings());
            }
            if (result.runnableFeatureResult() != null) {
                warnings.addAll(result.runnableFeatureResult().warnings());
            }
            if (result.missingYamlPatchSuggestions() != null) {
                for (var suggestion : result.missingYamlPatchSuggestions()) {
                    warnings.addAll(suggestion.warnings());
                }
            }
            return warnings;
        }

        private static AiGenerationAuditRecord buildAuditRecord(
                AiGenerationMode mode,
                AiAssistantProperties props,
                String requirement,
                Path requestedOutput,
                Path writtenOutput,
                GenerationResult result,
                List<String> blockingErrors,
                List<String> warnings
        ) {
            boolean parseOk = result != null && result.structuredResponse() != null && result.structuredResponse().parseSuccessful();
            boolean stepOk = result != null
                    && result.stepReuseValidationResult() != null
                    && result.stepReuseValidationResult().passed();
            boolean yamlOk = result != null
                    && result.yamlKeyValidationResult() != null
                    && result.yamlKeyValidationResult().passed();
            int reused = result != null && result.structuredResponse() != null ? result.structuredResponse().reusedSteps().size() : 0;
            int newSteps = result != null && result.structuredResponse() != null ? result.structuredResponse().newStepsNeeded().size() : 0;
            int missingYaml = result != null && result.yamlKeyValidationResult() != null ? result.yamlKeyValidationResult().missingCount() : 0;
            String outputPath = writtenOutput != null ? writtenOutput.toString() : requestedOutput.toString();

            return new AiGenerationAuditRecord(
                    UUID.randomUUID().toString(),
                    Instant.now().toString(),
                    "generate",
                    mode.name(),
                    props.model(),
                    props.promptVersion(),
                    AiGenerationAuditLogger.sha256(requirement),
                    outputPath,
                    parseOk,
                    stepOk,
                    yamlOk,
                    writtenOutput != null,
                    reused,
                    newSteps,
                    missingYaml,
                    warnings,
                    blockingErrors
            );
        }

        private String resolveRequirement() throws Exception {
            boolean hasFile = requirementFile != null;
            boolean hasText = requirement != null && !requirement.isBlank();
            if (hasFile && hasText) {
                throw new IllegalStateException("Use either -r/--requirement or --requirement-file, not both");
            }
            if (hasFile) {
                return Files.readString(requirementFile, StandardCharsets.UTF_8).trim();
            }
            if (hasText) {
                return requirement.trim();
            }
            throw new IllegalStateException("Provide -r/--requirement or --requirement-file");
        }
    }

    @Command(
            name = "serve",
            mixinStandardHelpOptions = true,
            description = "Localhost API: GET /health, POST /generate"
    )
    static final class ServeCommand implements Callable<Integer> {

        @Option(names = {"-p", "--port"}, description = "Port")
        int port = 8787;

        @Option(names = {"--project-root"}, description = "Repo root (default: user.dir)")
        Path projectRoot = Path.of(System.getProperty("user.dir"));

        @Override
        public Integer call() throws Exception {
            AiAssistantProperties props = new AiAssistantProperties();
            validateGemini(props);
            HttpServer server = AiGenerateHttpServer.createAndStart(port, projectRoot);
            System.out.println("http://127.0.0.1:" + port + "  POST /generate  GET /health");
            System.out.println("Press Enter to stop.");
            System.in.read();
            server.stop(0);
            return 0;
        }
    }

    @Command(
            name = "quality",
            mixinStandardHelpOptions = true,
            description = "Phase 3: scan .feature files — syntax checks + duplicate step report (JSON/HTML in target/)"
    )
    static final class QualityCommand implements Callable<Integer> {

        @Option(names = {"--project-root"}, description = "Repo root")
        Path projectRoot = Path.of(System.getProperty("user.dir"));

        @Option(names = {"--features-dir"}, description = "Relative to project root")
        String featuresDir = "src/test/resources/features";

        @Option(names = {"--strict"}, description = "Exit 1 if syntax errors or duplicate count ≥ fail threshold (see ai_policy.yml)")
        boolean strict;

        @Override
        public Integer call() throws Exception {
            var gate = new QualityGateService();
            var report = gate.run(projectRoot, featuresDir, strict);
            Path target = projectRoot.resolve("target");
            QualityReportWriter.write(target, report);
            System.out.println("Reports: " + target.resolve("ai-quality-report.json").toAbsolutePath());
            System.out.println("         " + target.resolve("ai-quality-report.html").toAbsolutePath());
            System.out.println("Syntax issues: " + report.syntaxIssues().size());
            System.out.println("Duplicate groups (≥ threshold): " + report.duplicateGroups().size());
            return report.failedStrict() ? 1 : 0;
        }
    }

    @Command(
            name = "triage",
            mixinStandardHelpOptions = true,
            description = "Phase 4: send log text to Gemini for likely area + summary (telemetry logged)"
    )
    static final class TriageCommand implements Callable<Integer> {

        @Option(names = {"--log"}, description = "Path to log file (UTF-8)")
        Path logFile;

        @Option(names = {"--text"}, description = "Inline log excerpt (if --log not set)")
        String text;

        @Override
        public Integer call() throws Exception {
            AiAssistantProperties props = new AiAssistantProperties();
            validateGemini(props);
            String excerpt;
            if (logFile != null) {
                excerpt = Files.readString(logFile, StandardCharsets.UTF_8);
            } else if (text != null && !text.isBlank()) {
                excerpt = text;
            } else {
                throw new IllegalStateException("Provide --log or --text");
            }
            if (excerpt.length() > 120_000) {
                excerpt = excerpt.substring(0, 120_000) + "\n...[truncated]";
            }
            var triage = new TriageService();
            String out = triage.triage(excerpt, props);
            System.out.println(out);
            Path tri = Path.of("target", "ai-triage-last.txt");
            Files.createDirectories(tri.getParent());
            Files.writeString(tri, out, StandardCharsets.UTF_8);
            System.out.println("Also saved: " + tri.toAbsolutePath());
            return 0;
        }
    }

    @Command(
            name = "telemetry-report",
            mixinStandardHelpOptions = true,
            description = "Phase 4: build target/ai-telemetry-summary.html from target/ai-telemetry.jsonl"
    )
    static final class TelemetryReportCommand implements Callable<Integer> {

        @Option(names = {"--project-root"}, description = "Repo root (target/ under it)")
        Path projectRoot = Path.of(System.getProperty("user.dir"));

        @Override
        public Integer call() throws Exception {
            Path html = TelemetrySummaryWriter.writeHtml(projectRoot.resolve("target"));
            System.out.println("Wrote: " + html.toAbsolutePath());
            return 0;
        }
    }

    static void validateGemini(AiAssistantProperties props) {
        if (props.apiKey() == null || props.apiKey().isBlank()) {
            throw new IllegalStateException(
                    "Set " + props.geminiApiKeyEnvName() + " to your Google AI Studio API key"
            );
        }
        if (props.model() == null || props.model().isBlank()) {
            throw new IllegalStateException("Set model in ai_assistant.yml or GEMINI_MODEL");
        }
    }
}

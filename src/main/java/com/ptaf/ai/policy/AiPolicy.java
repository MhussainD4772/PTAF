package com.ptaf.ai.policy;

import com.ptaf.ai.security.LogRedactor;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/** Loads optional {@code /config/ai_policy.yml} for Phase 4 guardrails. */
public final class AiPolicy {

    private final Map<String, Object> root;

    public AiPolicy() {
        this(load());
    }

    AiPolicy(Map<String, Object> root) {
        this.root = root;
    }

    private static Map<String, Object> load() {
        try (InputStream in = AiPolicy.class.getResourceAsStream("/config/ai_policy.yml")) {
            if (in == null) {
                return Collections.emptyMap();
            }
            Yaml yaml = new Yaml();
            @SuppressWarnings("unchecked")
            Map<String, Object> map = yaml.load(in);
            return map != null ? map : Collections.emptyMap();
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> policy() {
        Object v = root.get("ai_policy");
        if (v instanceof Map<?, ?> m) {
            return (Map<String, Object>) m;
        }
        return Collections.emptyMap();
    }

    public int maxRequirementChars() {
        Object v = policy().get("max_requirement_chars");
        if (v instanceof Number n) {
            return n.intValue();
        }
        return 20_000;
    }

    public int duplicateStepWarnThreshold() {
        Object v = policy().get("duplicate_step_warn_threshold");
        if (v instanceof Number n) {
            return n.intValue();
        }
        return 8;
    }

    public int duplicateStepFailThreshold() {
        Object v = policy().get("duplicate_step_fail_threshold");
        if (v instanceof Number n) {
            return n.intValue();
        }
        return 25;
    }

    public boolean redactTriageInput() {
        Object v = policy().get("redact_triage_input");
        if (v instanceof Boolean b) {
            return b;
        }
        return true;
    }

    /** Apply policy to optional log paste before Gemini triage. */
    public String maybeRedactTriageInput(String text) {
        if (text == null) {
            return null;
        }
        return redactTriageInput() ? LogRedactor.redact(text) : text;
    }

    public List<Pattern> blockedRequirementPatterns() {
        Object v = policy().get("blocked_requirement_patterns");
        if (!(v instanceof List<?> list)) {
            return List.of();
        }
        List<Pattern> out = new ArrayList<>();
        for (Object o : list) {
            if (o != null) {
                try {
                    out.add(Pattern.compile(o.toString()));
                } catch (Exception ignored) {
                    // skip invalid
                }
            }
        }
        return out;
    }

    /** @return null if OK, or rejection reason */
    public String validateRequirement(String requirement) {
        if (requirement == null) {
            return "requirement is null";
        }
        int max = maxRequirementChars();
        if (max > 0 && requirement.length() > max) {
            return "requirement exceeds max_requirement_chars (" + max + ")";
        }
        for (Pattern p : blockedRequirementPatterns()) {
            if (p.matcher(requirement).find()) {
                return "requirement matched blocked pattern: " + p.pattern();
            }
        }
        return null;
    }
}

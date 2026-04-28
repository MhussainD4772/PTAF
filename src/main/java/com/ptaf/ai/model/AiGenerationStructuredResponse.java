package com.ptaf.ai.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Strict structured contract extracted from model output.
 */
public final class AiGenerationStructuredResponse {
    private String featureFile;
    private List<String> reusedSteps;
    private List<String> newStepsNeeded;
    private List<String> yamlKeysUsed;
    private List<String> missingYamlKeys;
    private List<String> warnings;
    private boolean parseSuccessful;
    private List<String> parseErrors;

    public AiGenerationStructuredResponse() {
        this.featureFile = "";
        this.reusedSteps = new ArrayList<>();
        this.newStepsNeeded = new ArrayList<>();
        this.yamlKeysUsed = new ArrayList<>();
        this.missingYamlKeys = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.parseSuccessful = false;
        this.parseErrors = new ArrayList<>();
    }

    public String featureFile() {
        return featureFile;
    }

    public void setFeatureFile(String featureFile) {
        this.featureFile = featureFile;
    }

    public List<String> reusedSteps() {
        return reusedSteps;
    }

    public void setReusedSteps(List<String> reusedSteps) {
        this.reusedSteps = reusedSteps;
    }

    public List<String> newStepsNeeded() {
        return newStepsNeeded;
    }

    public void setNewStepsNeeded(List<String> newStepsNeeded) {
        this.newStepsNeeded = newStepsNeeded;
    }

    public List<String> yamlKeysUsed() {
        return yamlKeysUsed;
    }

    public void setYamlKeysUsed(List<String> yamlKeysUsed) {
        this.yamlKeysUsed = yamlKeysUsed;
    }

    public List<String> missingYamlKeys() {
        return missingYamlKeys;
    }

    public void setMissingYamlKeys(List<String> missingYamlKeys) {
        this.missingYamlKeys = missingYamlKeys;
    }

    public List<String> warnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public boolean parseSuccessful() {
        return parseSuccessful;
    }

    public void setParseSuccessful(boolean parseSuccessful) {
        this.parseSuccessful = parseSuccessful;
    }

    public List<String> parseErrors() {
        return parseErrors;
    }

    public void setParseErrors(List<String> parseErrors) {
        this.parseErrors = parseErrors;
    }
}

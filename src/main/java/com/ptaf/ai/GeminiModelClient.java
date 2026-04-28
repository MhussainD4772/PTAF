package com.ptaf.ai;

import com.ptaf.ai.config.AiAssistantProperties;

/**
 * Production model client backed by {@link GeminiClient}.
 */
public final class GeminiModelClient implements AiModelClient {
    private final GeminiClient delegate;

    public GeminiModelClient() {
        this.delegate = new GeminiClient();
    }

    @Override
    public String generate(String systemPrompt, String userPrompt, AiAssistantProperties props) throws Exception {
        return delegate.generateContent(systemPrompt, userPrompt, props);
    }
}

package com.ptaf.ai;

import com.ptaf.ai.config.AiAssistantProperties;

/**
 * Abstraction for model providers (Gemini now, others later).
 */
public interface AiModelClient {
    String generate(String systemPrompt, String userPrompt, AiAssistantProperties props) throws Exception;
}

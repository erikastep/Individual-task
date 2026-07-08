package com.accenture.springai_bootcamp_demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** App-level chat settings ({@code app.chat.*}), e.g. the system prompt. */
@ConfigurationProperties(prefix = "app.chat")
public record AiChatProperties(String systemPrompt) {

    public AiChatProperties {
        if (systemPrompt == null || systemPrompt.isBlank()) {
            systemPrompt = "You are a helpful, concise assistant.";
        }
    }
}

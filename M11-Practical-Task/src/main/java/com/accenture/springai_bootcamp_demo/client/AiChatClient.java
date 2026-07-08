package com.accenture.springai_bootcamp_demo.client;

import com.accenture.springai_bootcamp_demo.config.AiChatProperties;
import com.accenture.springai_bootcamp_demo.entity.ChatMessage;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** Wraps Spring AI's {@link ChatClient} to send chat history to the local Ollama model. */
@Slf4j
@Component
public class AiChatClient {

    private final ChatClient chatClient;
    private final AiChatProperties properties;

    public AiChatClient(ChatClient.Builder chatClientBuilder, AiChatProperties properties) {
        this.chatClient = chatClientBuilder.build();
        this.properties = properties;
    }

    public String complete(List<ChatMessage> history) {
        String reply = call(history);
        return extractContent(reply);
    }

    private String call(List<ChatMessage> history) {
        try {
            return chatClient.prompt()
                    .messages(toModelMessages(history))
                    .call()
                    .content();
        } catch (RuntimeException ex) {
            log.error("Chat model request failed", ex);
            throw new AiChatException("Failed to reach the chat model: " + ex.getMessage(), ex);
        }
    }

    private List<Message> toModelMessages(List<ChatMessage> history) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(properties.systemPrompt()));
        for (ChatMessage message : history) {
            messages.add(toModelMessage(message));
        }
        return messages;
    }

    private Message toModelMessage(ChatMessage message) {
        return switch (message.getRole()) {
            case USER -> new UserMessage(message.getContent());
            case ASSISTANT -> new AssistantMessage(message.getContent());
            case SYSTEM -> new SystemMessage(message.getContent());
        };
    }

    private String extractContent(String content) {
        if (!StringUtils.hasText(content)) {
            throw new AiChatException("The chat model returned an empty response");
        }
        return content.trim();
    }
}

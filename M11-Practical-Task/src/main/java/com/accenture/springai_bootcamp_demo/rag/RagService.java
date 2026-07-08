package com.accenture.springai_bootcamp_demo.rag;

import com.accenture.springai_bootcamp_demo.rag.KnowledgeBase.Passage;
import com.accenture.springai_bootcamp_demo.rag.dto.RagAnswer;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * RAG over the knowledge base: retrieve the most relevant passages, then ask the
 * local model to answer using only those — grounding answers in trusted docs.
 */
@Slf4j
@Service
public class RagService {

    private static final int TOP_K = 3;

    private final ChatClient chatClient;
    private final KnowledgeBase knowledgeBase;

    public RagService(ChatClient.Builder chatClientBuilder, KnowledgeBase knowledgeBase) {
        this.chatClient = chatClientBuilder.build();
        this.knowledgeBase = knowledgeBase;
    }

    public RagAnswer ask(String question) {
        List<Passage> passages = retrieve(question);
        String context = passages.stream()
                .map(p -> "[%s] %s".formatted(p.source(), p.text()))
                .collect(Collectors.joining("\n\n"));

        String answer = generate(question, context);
        List<String> sources = passages.stream().map(Passage::source).distinct().toList();
        return new RagAnswer(answer, sources);
    }

    private List<Passage> retrieve(String question) {
        try {
            List<Passage> passages = knowledgeBase.search(question, TOP_K);
            if (passages.isEmpty()) {
                throw new RagException("No knowledge documents are loaded.");
            }
            return passages;
        } catch (RagException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Knowledge retrieval failed", ex);
            throw new RagException("Knowledge base unavailable (is the embedding model pulled?): " + ex.getMessage(), ex);
        }
    }

    private String generate(String question, String context) {
        try {
            return chatClient.prompt()
                    .system("""
                            Answer the question using ONLY the CONTEXT below. If the answer is not in
                            the context, say you don't know. Be concise and do not invent details.
                            """)
                    .user("CONTEXT:%n%s%n%nQUESTION: %s".formatted(context, question))
                    .call()
                    .content()
                    .trim();
        } catch (RuntimeException ex) {
            log.error("RAG answer generation failed", ex);
            throw new RagException("Failed to answer the question: " + ex.getMessage(), ex);
        }
    }
}

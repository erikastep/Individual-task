package com.accenture.springai_bootcamp_demo.rag;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * In-memory vector store for RAG: loads {@code classpath:/docs} on first use,
 * embeds each passage via Ollama, and ranks them against a query by cosine similarity.
 */
@Slf4j
@Component
public class KnowledgeBase {

    private final EmbeddingModel embeddingModel;
    private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    private final List<Chunk> chunks = new ArrayList<>();
    private volatile boolean loaded = false;

    public KnowledgeBase(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public record Passage(String text, String source) {
    }

    private record Chunk(String text, String source, float[] embedding) {
    }

    public List<Passage> search(String query, int topK) {
        ensureLoaded();
        float[] queryEmbedding = embeddingModel.embed(query);
        return chunks.stream()
                .sorted(Comparator.comparingDouble(chunk -> -cosine(queryEmbedding, chunk.embedding())))
                .limit(topK)
                .map(chunk -> new Passage(chunk.text(), chunk.source()))
                .toList();
    }

    private synchronized void ensureLoaded() {
        if (loaded) {
            return;
        }
        try {
            Resource[] resources = resolver.getResources("classpath:/docs/*.txt");
            for (Resource resource : resources) {
                ingest(resource);
            }
            log.info("Loaded {} knowledge passages from {} documents", chunks.size(), resources.length);
            loaded = true;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to load knowledge documents: " + ex.getMessage(), ex);
        }
    }

    private void ingest(Resource resource) throws Exception {
        String content = resource.getContentAsString(StandardCharsets.UTF_8);
        String source = resource.getFilename();
        for (String paragraph : content.split("\\n\\s*\\n")) {
            String text = paragraph.strip();
            if (!text.isBlank()) {
                chunks.add(new Chunk(text, source, embeddingModel.embed(text)));
            }
        }
    }

    private static double cosine(float[] a, float[] b) {
        double dot = 0;
        double normA = 0;
        double normB = 0;
        for (int i = 0; i < a.length && i < b.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-10);
    }
}

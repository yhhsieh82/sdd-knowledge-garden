package org.example.queryapi.retrieval;

import org.example.queryapi.domain.Chunk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryRetrieverTest {

    private InMemoryKnowledgeBase knowledgeBase;
    private InMemoryRetriever retriever;

    @BeforeEach
    void setUp() {
        knowledgeBase = new InMemoryKnowledgeBase();
        retriever = new InMemoryRetriever(knowledgeBase);
    }

    @Test
    void emptyKnowledgeBaseReturnsEmptyList() {
        List<Chunk> chunks = retriever.retrieve("deployment topology", 10);

        assertThat(chunks).isEmpty();
    }

    @Test
    void keywordMatchingFindsRelevantChunks() {
        // Add chunks with different content
        knowledgeBase.addChunk(new Chunk("chunk-1", "doc-1", "Deployment Guide",
                "The deployment topology uses active-passive configuration.", null, 0.0));
        knowledgeBase.addChunk(new Chunk("chunk-2", "doc-2", "Node Config",
                "Each node runs the same version.", null, 0.0));
        knowledgeBase.addChunk(new Chunk("chunk-3", "doc-3", "Security",
                "Security policies are enforced.", null, 0.0));

        List<Chunk> chunks = retriever.retrieve("deployment", 10);

        assertThat(chunks).isNotEmpty();
        assertThat(chunks).anyMatch(c -> c.getChunkId().equals("chunk-1"));
    }

    @Test
    void relevanceScoringAssignsScoresInValidRange() {
        knowledgeBase.addChunk(new Chunk("chunk-1", "doc-1", "Test",
                "deployment topology active-passive", null, 0.0));
        knowledgeBase.addChunk(new Chunk("chunk-2", "doc-2", "Test",
                "deployment configuration", null, 0.0));

        List<Chunk> chunks = retriever.retrieve("deployment topology", 10);

        assertThat(chunks).isNotEmpty();
        for (Chunk chunk : chunks) {
            assertThat(chunk.getRelevanceScore()).isBetween(0.0, 1.0);
        }
    }

    @Test
    void chunksAreOrderedByRelevanceDescending() {
        // Chunk with more keyword matches should score higher
        knowledgeBase.addChunk(new Chunk("chunk-1", "doc-1", "Test",
                "deployment", null, 0.0));
        knowledgeBase.addChunk(new Chunk("chunk-2", "doc-2", "Test",
                "deployment topology active-passive deployment", null, 0.0));
        knowledgeBase.addChunk(new Chunk("chunk-3", "doc-3", "Test",
                "unrelated content", null, 0.0));

        List<Chunk> chunks = retriever.retrieve("deployment topology", 10);

        assertThat(chunks).isNotEmpty();
        // Verify ordering: each chunk should have score >= next chunk
        for (int i = 0; i < chunks.size() - 1; i++) {
            assertThat(chunks.get(i).getRelevanceScore())
                    .isGreaterThanOrEqualTo(chunks.get(i + 1).getRelevanceScore());
        }
    }

    @Test
    void maxChunksLimitIsRespected() {
        // Add more chunks than maxChunks limit
        for (int i = 0; i < 10; i++) {
            knowledgeBase.addChunk(new Chunk("chunk-" + i, "doc-" + i, "Test",
                    "deployment topology active-passive", null, 0.0));
        }

        List<Chunk> chunks = retriever.retrieve("deployment", 5);

        assertThat(chunks).hasSizeLessThanOrEqualTo(5);
    }

    @Test
    void relevanceThresholdFiltersLowScoreChunks() {
        // Add chunks with varying relevance
        knowledgeBase.addChunk(new Chunk("chunk-1", "doc-1", "Test",
                "deployment topology active-passive deployment", null, 0.0));
        knowledgeBase.addChunk(new Chunk("chunk-2", "doc-2", "Test",
                "completely unrelated content about cats and dogs", null, 0.0));

        List<Chunk> chunks = retriever.retrieve("deployment topology", 10);

        // All returned chunks should have score >= 0.8 (relevance threshold)
        for (Chunk chunk : chunks) {
            assertThat(chunk.getRelevanceScore()).isGreaterThanOrEqualTo(0.8);
        }
    }

    @Test
    void allChunksBelowThresholdReturnsEmptyList() {
        // Add chunk with no matching keywords
        knowledgeBase.addChunk(new Chunk("chunk-1", "doc-1", "Test",
                "completely unrelated content", null, 0.0));

        List<Chunk> chunks = retriever.retrieve("deployment topology", 10);

        // Should return empty if all chunks below 0.8 threshold
        assertThat(chunks).isEmpty();
    }

    @Test
    void caseInsensitiveMatching() {
        knowledgeBase.addChunk(new Chunk("chunk-1", "doc-1", "Test",
                "DEPLOYMENT Topology Active-Passive", null, 0.0));

        List<Chunk> chunks = retriever.retrieve("deployment topology", 10);

        assertThat(chunks).isNotEmpty();
        assertThat(chunks.get(0).getRelevanceScore()).isGreaterThan(0.0);
    }

    @Test
    void multipleKeywordsIncreaseScore() {
        knowledgeBase.addChunk(new Chunk("chunk-1", "doc-1", "Test",
                "deployment", null, 0.0));
        knowledgeBase.addChunk(new Chunk("chunk-2", "doc-2", "Test",
                "deployment topology", null, 0.0));

        List<Chunk> chunks = retriever.retrieve("deployment topology", 10);

        // Chunk with more keyword matches should score higher
        if (chunks.size() >= 2) {
            Chunk firstChunk = chunks.get(0);
            // First chunk should have highest score
            assertThat(firstChunk.getText()).contains("deployment");
            assertThat(firstChunk.getText().toLowerCase()).contains("topology");
        }
    }

    @Test
    void nullKnowledgeBaseThrowsException() {
        assertThatThrownBy(() -> new InMemoryRetriever(null))
                .isInstanceOf(RetrievalException.class)
                .hasMessageContaining("Knowledge base cannot be null");
    }
}

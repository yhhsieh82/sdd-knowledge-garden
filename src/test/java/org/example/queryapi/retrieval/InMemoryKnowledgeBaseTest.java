package org.example.queryapi.retrieval;

import org.example.queryapi.domain.Chunk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryKnowledgeBaseTest {

    private InMemoryKnowledgeBase knowledgeBase;

    @BeforeEach
    void setUp() {
        knowledgeBase = new InMemoryKnowledgeBase();
    }

    @Test
    void emptyKnowledgeBaseReturnsEmptyList() {
        List<Chunk> chunks = knowledgeBase.getAllChunks();

        assertThat(chunks).isEmpty();
    }

    @Test
    void addChunkStoresChunk() {
        Chunk chunk = new Chunk("chunk-1", "doc-1", "Test Doc", "Test content", null, 0.0);

        knowledgeBase.addChunk(chunk);
        List<Chunk> chunks = knowledgeBase.getAllChunks();

        assertThat(chunks).hasSize(1);
        assertThat(chunks.get(0).getChunkId()).isEqualTo("chunk-1");
    }

    @Test
    void addMultipleChunksStoresAllChunks() {
        Chunk chunk1 = new Chunk("chunk-1", "doc-1", "Test Doc 1", "Content 1", null, 0.0);
        Chunk chunk2 = new Chunk("chunk-2", "doc-2", "Test Doc 2", "Content 2", null, 0.0);

        knowledgeBase.addChunk(chunk1);
        knowledgeBase.addChunk(chunk2);
        List<Chunk> chunks = knowledgeBase.getAllChunks();

        assertThat(chunks).hasSize(2);
    }

    @Test
    void clearRemovesAllChunks() {
        knowledgeBase.addChunk(new Chunk("chunk-1", "doc-1", "Test Doc", "Content", null, 0.0));
        knowledgeBase.addChunk(new Chunk("chunk-2", "doc-2", "Test Doc", "Content", null, 0.0));

        knowledgeBase.clear();
        List<Chunk> chunks = knowledgeBase.getAllChunks();

        assertThat(chunks).isEmpty();
    }
}

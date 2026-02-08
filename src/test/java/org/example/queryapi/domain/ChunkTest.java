package org.example.queryapi.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChunkTest {

    @Test
    void createsChunkWithAllRequiredFields() {
        Chunk chunk = new Chunk(
                "chunk-1",
                "doc-1",
                "Document Title",
                "This is the chunk text content.",
                "https://example.com/doc1",
                0.85
        );

        assertThat(chunk.getChunkId()).isEqualTo("chunk-1");
        assertThat(chunk.getDocumentId()).isEqualTo("doc-1");
        assertThat(chunk.getDocumentTitle()).isEqualTo("Document Title");
        assertThat(chunk.getText()).isEqualTo("This is the chunk text content.");
        assertThat(chunk.getUrl()).isEqualTo("https://example.com/doc1");
        assertThat(chunk.getRelevanceScore()).isEqualTo(0.85);
    }

    @Test
    void createsChunkWithNullUrl() {
        Chunk chunk = new Chunk(
                "chunk-1",
                "doc-1",
                "Document Title",
                "This is the chunk text content.",
                null,
                0.85
        );

        assertThat(chunk.getUrl()).isNull();
        assertThat(chunk.getChunkId()).isNotNull();
    }
}

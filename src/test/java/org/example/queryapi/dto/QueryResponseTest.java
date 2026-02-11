package org.example.queryapi.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QueryResponseTest {

    @Test
    void createsQueryResponseWithAllFields() {
        CitedDocument doc1 = new CitedDocument("doc-1", "Deployment Guide", "...", null);
        CitedDocument doc2 = new CitedDocument("doc-2", "Node Configuration", "...", null);
        ResponseMetadata metadata = new ResponseMetadata(5, 2, 1250L);

        QueryResponse response = new QueryResponse(
                "The recommended topology is active-passive [1]. Each node runs the same version [2].",
                true,
                List.of(doc1, doc2),
                metadata
        );

        assertThat(response.getAnswer()).contains("active-passive");
        assertThat(response.isAnswerSynthesized()).isTrue();
        assertThat(response.getCitedDocuments()).hasSize(2);
        assertThat(response.getMetadata().getProcessingTimeMs()).isEqualTo(1250L);
        assertThat(response.getMetadata().getTotalChunksRetrieved()).isEqualTo(5);
        assertThat(response.getMetadata().getTotalDocumentsCited()).isEqualTo(2);
    }
}

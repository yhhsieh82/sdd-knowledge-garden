package org.example.queryapi.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QueryResponseTest {

    @Test
    void createsQueryResponseWithAllFields() {
        CitedDocument doc1 = new CitedDocument("doc-1", "Deployment Guide", "...", null);
        CitedDocument doc2 = new CitedDocument("doc-2", "Node Configuration", "...", null);
        ResponseMetadata metadata = new ResponseMetadata(1250L, true, 5);

        QueryResponse response = new QueryResponse(
                "The recommended topology is active-passive [1]. Each node runs the same version [2].",
                List.of(doc1, doc2),
                metadata
        );

        assertThat(response.getAnswer()).contains("active-passive");
        assertThat(response.getCitedDocuments()).hasSize(2);
        assertThat(response.getMetadata().getProcessingTimeMs()).isEqualTo(1250L);
        assertThat(response.getMetadata().isAnswerSynthesized()).isTrue();
        assertThat(response.getMetadata().getChunksRetrieved()).isEqualTo(5);
    }
}

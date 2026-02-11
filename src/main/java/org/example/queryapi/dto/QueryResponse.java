package org.example.queryapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Response DTO for the query endpoint.
 * Contains the answer with citations and execution metadata.
 */
@Schema(description = "Query response containing the answer, cited documents, and metadata")
public class QueryResponse {

    @Schema(description = "Natural-language answer with inline citation markers [1], [2], etc.",
            example = "The recommended topology is active-passive [1]. Each node runs the same version [2].")
    private String answer;

    @Schema(description = "Whether an answer was successfully synthesized", example = "true")
    private boolean answerSynthesized;

    @Schema(description = "List of cited source documents in order of first citation appearance")
    private List<CitedDocument> citedDocuments;

    @Schema(description = "Execution metadata")
    private ResponseMetadata metadata;

    public QueryResponse() {
    }

    public QueryResponse(String answer, boolean answerSynthesized, List<CitedDocument> citedDocuments, ResponseMetadata metadata) {
        this.answer = answer;
        this.answerSynthesized = answerSynthesized;
        this.citedDocuments = citedDocuments;
        this.metadata = metadata;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isAnswerSynthesized() {
        return answerSynthesized;
    }

    public void setAnswerSynthesized(boolean answerSynthesized) {
        this.answerSynthesized = answerSynthesized;
    }

    public List<CitedDocument> getCitedDocuments() {
        return citedDocuments;
    }

    public void setCitedDocuments(List<CitedDocument> citedDocuments) {
        this.citedDocuments = citedDocuments;
    }

    public ResponseMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ResponseMetadata metadata) {
        this.metadata = metadata;
    }
}

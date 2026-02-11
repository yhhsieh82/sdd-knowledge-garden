package org.example.queryapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO containing execution metadata for the query response.
 */
@Schema(description = "Execution metadata for the query")
public class ResponseMetadata {

    @Schema(description = "Total processing time in milliseconds", example = "1250")
    private long processingTimeMs;

    @Schema(description = "Number of chunks retrieved from knowledge base", example = "5")
    private int totalChunksRetrieved;

    @Schema(description = "Number of unique documents cited in the answer", example = "3")
    private int totalDocumentsCited;

    public ResponseMetadata() {
    }

    public ResponseMetadata(int totalChunksRetrieved, int totalDocumentsCited, long processingTimeMs) {
        this.totalChunksRetrieved = totalChunksRetrieved;
        this.totalDocumentsCited = totalDocumentsCited;
        this.processingTimeMs = processingTimeMs;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public int getTotalChunksRetrieved() {
        return totalChunksRetrieved;
    }

    public void setTotalChunksRetrieved(int totalChunksRetrieved) {
        this.totalChunksRetrieved = totalChunksRetrieved;
    }

    public int getTotalDocumentsCited() {
        return totalDocumentsCited;
    }

    public void setTotalDocumentsCited(int totalDocumentsCited) {
        this.totalDocumentsCited = totalDocumentsCited;
    }
}

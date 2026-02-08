package org.example.queryapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO containing execution metadata for the query response.
 */
@Schema(description = "Execution metadata for the query")
public class ResponseMetadata {

    @Schema(description = "Total processing time in milliseconds", example = "1250")
    private long processingTimeMs;

    @Schema(description = "Whether an answer was successfully synthesized", example = "true")
    private boolean answerSynthesized;

    @Schema(description = "Number of chunks retrieved from knowledge base", example = "5")
    private int chunksRetrieved;

    public ResponseMetadata() {
    }

    public ResponseMetadata(long processingTimeMs, boolean answerSynthesized, int chunksRetrieved) {
        this.processingTimeMs = processingTimeMs;
        this.answerSynthesized = answerSynthesized;
        this.chunksRetrieved = chunksRetrieved;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public boolean isAnswerSynthesized() {
        return answerSynthesized;
    }

    public void setAnswerSynthesized(boolean answerSynthesized) {
        this.answerSynthesized = answerSynthesized;
    }

    public int getChunksRetrieved() {
        return chunksRetrieved;
    }

    public void setChunksRetrieved(int chunksRetrieved) {
        this.chunksRetrieved = chunksRetrieved;
    }
}

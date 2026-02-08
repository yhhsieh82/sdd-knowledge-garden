package org.example.queryapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

/**
 * DTO for structured error responses.
 * Used for 400 and 503 errors.
 */
@Schema(description = "Structured error response")
public class ErrorResponse {

    @Schema(description = "Error code", example = "VALIDATION_ERROR", 
            allowableValues = {"VALIDATION_ERROR", "RETRIEVAL_FAILED", "SYNTHESIS_FAILED"})
    private String error;

    @Schema(description = "Human-readable error message", 
            example = "Query must be non-blank and at most 2000 characters.")
    private String message;

    @Schema(description = "Optional additional error details", 
            example = "{\"field\": \"query\"}")
    private Map<String, Object> details;

    public ErrorResponse() {
    }

    public ErrorResponse(String error, String message, Map<String, Object> details) {
        this.error = error;
        this.message = message;
        this.details = details;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}

package org.example.queryapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for the query endpoint.
 * Validation constraints define the API contract.
 */
@Schema(description = "Query request containing the natural-language question and optional parameters")
public class QueryRequest {

    @NotBlank(message = "Query must not be blank")
    @Size(max = 2000, message = "Query must not exceed 2000 characters")
    @Schema(description = "Natural-language question", example = "What is the recommended deployment topology?", requiredMode = Schema.RequiredMode.REQUIRED)
    private String query;

    @Min(value = 1, message = "maxSources must be at least 1")
    @Max(value = 50, message = "maxSources must not exceed 50")
    @Schema(description = "Maximum number of document chunks to use for synthesis", example = "5")
    private Integer maxSources;

    @Min(value = 1, message = "maxTokens must be at least 1")
    @Schema(description = "Maximum tokens for the generated answer", example = "100")
    private Integer maxTokens;

    // Constructor for Jackson
    public QueryRequest() {
    }

    public QueryRequest(String query, Integer maxSources, Integer maxTokens) {
        this.query = query;
        this.maxSources = maxSources;
        this.maxTokens = maxTokens;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getMaxSources() {
        return maxSources;
    }

    public void setMaxSources(Integer maxSources) {
        this.maxSources = maxSources;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }
}

package org.example.queryapi.dto;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    void createsErrorResponseWithAllFields() {
        ErrorResponse response = new ErrorResponse(
                "VALIDATION_ERROR",
                "Query must be non-blank and at most 2000 characters.",
                Map.of("field", "query")
        );

        assertThat(response.getError()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getMessage()).contains("non-blank");
        assertThat(response.getDetails()).containsEntry("field", "query");
    }

    @Test
    void createsErrorResponseWithoutDetails() {
        ErrorResponse response = new ErrorResponse(
                "SYNTHESIS_FAILED",
                "LLM synthesis timed out.",
                null
        );

        assertThat(response.getError()).isEqualTo("SYNTHESIS_FAILED");
        assertThat(response.getMessage()).isEqualTo("LLM synthesis timed out.");
        assertThat(response.getDetails()).isNull();
    }
}

package org.example.queryapi.validation;

import org.example.queryapi.dto.QueryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class QueryValidatorTest {

    private QueryValidator validator;

    @BeforeEach
    void setUp() {
        validator = new QueryValidatorImpl();
    }

    @Test
    void validRequestPassesValidation() {
        QueryRequest request = new QueryRequest("What is the deployment topology?", 5, 100);

        ValidationResult result = validator.validate(request);

        assertThat(result.isValid()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRequests")
    void invalidRequestFailsValidation(QueryRequest request, String expectedErrorCode, String expectedFieldInMessage) {
        ValidationResult result = validator.validate(request);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrorCode()).isEqualTo(expectedErrorCode);
        assertThat(result.getErrorMessage()).containsIgnoringCase(expectedFieldInMessage);
    }

    private static Stream<Arguments> provideInvalidRequests() {
        return Stream.of(
                // Null query
                Arguments.of(
                        new QueryRequest(null, 5, 100),
                        "VALIDATION_ERROR",
                        "query"
                ),
                // Blank query (empty string)
                Arguments.of(
                        new QueryRequest("", 5, 100),
                        "VALIDATION_ERROR",
                        "query"
                ),
                // Blank query (whitespace only)
                Arguments.of(
                        new QueryRequest("   ", 5, 100),
                        "VALIDATION_ERROR",
                        "query"
                ),
                // Query exceeding max length (2000 chars)
                Arguments.of(
                        new QueryRequest("a".repeat(2001), 5, 100),
                        "VALIDATION_ERROR",
                        "query"
                ),
                // maxSources below minimum (1)
                Arguments.of(
                        new QueryRequest("What is the deployment topology?", 0, 100),
                        "VALIDATION_ERROR",
                        "maxSources"
                ),
                // maxSources above maximum (50)
                Arguments.of(
                        new QueryRequest("What is the deployment topology?", 51, 100),
                        "VALIDATION_ERROR",
                        "maxSources"
                ),
                // maxTokens below minimum (1)
                Arguments.of(
                        new QueryRequest("What is the deployment topology?", 5, 0),
                        "VALIDATION_ERROR",
                        "maxTokens"
                )
        );
    }

    @Test
    void validRequestWithNullOptionalFieldsPassesValidation() {
        QueryRequest request = new QueryRequest("What is the deployment topology?", null, null);

        ValidationResult result = validator.validate(request);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validRequestWithMaxLengthQueryPassesValidation() {
        // Query exactly at max length (2000 chars)
        String maxLengthQuery = "a".repeat(2000);
        QueryRequest request = new QueryRequest(maxLengthQuery, 5, 100);

        ValidationResult result = validator.validate(request);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validRequestWithBoundaryMaxSourcesPassesValidation() {
        // maxSources at boundary values
        QueryRequest request1 = new QueryRequest("What is the deployment topology?", 1, 100);
        QueryRequest request50 = new QueryRequest("What is the deployment topology?", 50, 100);

        ValidationResult result1 = validator.validate(request1);
        ValidationResult result50 = validator.validate(request50);

        assertThat(result1.isValid()).isTrue();
        assertThat(result50.isValid()).isTrue();
    }

    @Test
    void validationErrorIncludesFieldDetails() {
        QueryRequest request = new QueryRequest(null, 5, 100);

        ValidationResult result = validator.validate(request);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getDetails()).isNotNull();
        assertThat(result.getDetails()).containsKey("field");
    }
}

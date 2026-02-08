package org.example.queryapi.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class QueryRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void validQueryRequestPassesValidation() {
        QueryRequest request = new QueryRequest("What is the deployment topology?", 5, 100);

        Set<ConstraintViolation<QueryRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void nullQueryFailsValidation() {
        QueryRequest request = new QueryRequest(null, 5, 100);

        Set<ConstraintViolation<QueryRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("query"));
    }

    @Test
    void blankQueryFailsValidation() {
        QueryRequest request = new QueryRequest("   ", 5, 100);

        Set<ConstraintViolation<QueryRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("query"));
    }

    @Test
    void queryExceedingMaxLengthFailsValidation() {
        String longQuery = "a".repeat(2001);
        QueryRequest request = new QueryRequest(longQuery, 5, 100);

        Set<ConstraintViolation<QueryRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("query"));
    }

    @Test
    void maxSourcesBelowMinimumFailsValidation() {
        QueryRequest request = new QueryRequest("What is the deployment topology?", 0, 100);

        Set<ConstraintViolation<QueryRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("maxSources"));
    }

    @Test
    void maxSourcesAboveMaximumFailsValidation() {
        QueryRequest request = new QueryRequest("What is the deployment topology?", 51, 100);

        Set<ConstraintViolation<QueryRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("maxSources"));
    }

    @Test
    void maxTokensBelowMinimumFailsValidation() {
        QueryRequest request = new QueryRequest("What is the deployment topology?", 5, 0);

        Set<ConstraintViolation<QueryRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("maxTokens"));
    }

    @Test
    void optionalFieldsCanBeNull() {
        QueryRequest request = new QueryRequest("What is the deployment topology?", null, null);

        Set<ConstraintViolation<QueryRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }
}

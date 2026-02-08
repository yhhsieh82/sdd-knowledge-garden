package org.example.queryapi.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.example.queryapi.dto.QueryRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of QueryValidator using Bean Validation.
 * Maps Bean Validation constraint violations to ValidationResult.
 */
@Component
public class QueryValidatorImpl implements QueryValidator {

    private final Validator validator;

    public QueryValidatorImpl() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public ValidationResult validate(QueryRequest request) {
        // Use Bean Validation to validate the request
        Set<ConstraintViolation<QueryRequest>> violations = validator.validate(request);

        if (violations.isEmpty()) {
            return ValidationResult.success();
        }

        // Map first violation to ValidationResult
        ConstraintViolation<QueryRequest> firstViolation = violations.iterator().next();
        String field = firstViolation.getPropertyPath().toString();
        String message = buildErrorMessage(firstViolation);
        
        Map<String, Object> details = new HashMap<>();
        details.put("field", field);

        return ValidationResult.failure("VALIDATION_ERROR", message, details);
    }

    private String buildErrorMessage(ConstraintViolation<QueryRequest> violation) {
        String field = violation.getPropertyPath().toString();
        String defaultMessage = violation.getMessage();
        
        // Build a user-friendly error message
        return String.format("Validation failed for field '%s': %s", field, defaultMessage);
    }
}

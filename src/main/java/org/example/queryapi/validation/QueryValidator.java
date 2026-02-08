package org.example.queryapi.validation;

import org.example.queryapi.dto.QueryRequest;

/**
 * Validator for QueryRequest.
 * Uses Bean Validation and handles custom validation logic.
 */
public interface QueryValidator {
    
    /**
     * Validates a query request.
     * 
     * @param request the request to validate
     * @return validation result with success/failure status and error details
     */
    ValidationResult validate(QueryRequest request);
}

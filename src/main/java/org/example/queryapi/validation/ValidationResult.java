package org.example.queryapi.validation;

import java.util.HashMap;
import java.util.Map;

/**
 * Result of validation operation.
 * Contains success/failure status and error details if validation failed.
 */
public class ValidationResult {
    private final boolean valid;
    private final String errorCode;
    private final String errorMessage;
    private final Map<String, Object> details;

    private ValidationResult(boolean valid, String errorCode, String errorMessage, Map<String, Object> details) {
        this.valid = valid;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.details = details;
    }

    public static ValidationResult success() {
        return new ValidationResult(true, null, null, null);
    }

    public static ValidationResult failure(String errorCode, String errorMessage, Map<String, Object> details) {
        return new ValidationResult(false, errorCode, errorMessage, details);
    }

    public static ValidationResult failure(String errorCode, String errorMessage) {
        return new ValidationResult(false, errorCode, errorMessage, new HashMap<>());
    }

    public boolean isValid() {
        return valid;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}

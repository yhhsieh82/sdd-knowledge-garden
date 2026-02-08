package org.example.queryapi.retrieval;

/**
 * Exception thrown when retrieval fails.
 * Used to signal store/network errors to the API layer.
 */
public class RetrievalException extends RuntimeException {
    
    public RetrievalException(String message) {
        super(message);
    }
    
    public RetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}

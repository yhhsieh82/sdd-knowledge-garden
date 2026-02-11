package org.example.queryapi.synthesis;

/**
 * Exception thrown when synthesis fails.
 * Used to signal LLM timeout, API errors, or other synthesis failures.
 */
public class SynthesisException extends RuntimeException {
    
    public SynthesisException(String message) {
        super(message);
    }
    
    public SynthesisException(String message, Throwable cause) {
        super(message, cause);
    }
}

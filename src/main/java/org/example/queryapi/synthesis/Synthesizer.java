package org.example.queryapi.synthesis;

import org.example.queryapi.domain.Chunk;
import org.example.queryapi.domain.SynthesisResult;

import java.util.List;

/**
 * Interface for synthesizing answers from retrieved chunks using LLM.
 * Implementations can use different LLM providers.
 */
public interface Synthesizer {
    
    /**
     * Synthesizes an answer from the query and retrieved chunks.
     * 
     * @param query the user's question
     * @param chunks the retrieved chunks to use as context
     * @return synthesis result with answer text and citations
     * @throws SynthesisException if synthesis fails (timeout, API error, etc.)
     */
    SynthesisResult synthesize(String query, List<Chunk> chunks);
}

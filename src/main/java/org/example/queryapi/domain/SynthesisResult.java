package org.example.queryapi.domain;

import java.util.List;

/**
 * Domain model representing the result of LLM synthesis.
 * Contains the generated answer with citation markers and the list of cited chunk IDs.
 */
public class SynthesisResult {
    private final String answerText;
    private final List<String> citedChunkIds;

    public SynthesisResult(String answerText, List<String> citedChunkIds) {
        this.answerText = answerText;
        this.citedChunkIds = citedChunkIds;
    }

    public String getAnswerText() {
        return answerText;
    }

    public List<String> getCitedChunkIds() {
        return citedChunkIds;
    }
}

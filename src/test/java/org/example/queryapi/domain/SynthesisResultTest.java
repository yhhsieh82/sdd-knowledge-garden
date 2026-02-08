package org.example.queryapi.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SynthesisResultTest {

    @Test
    void createsSynthesisResultWithAnswerAndCitations() {
        String answerText = "The deployment topology is active-passive [1]. Each node runs the same version [2].";
        List<String> citedChunkIds = List.of("chunk-1", "chunk-2");

        SynthesisResult result = new SynthesisResult(answerText, citedChunkIds);

        assertThat(result.getAnswerText()).isEqualTo(answerText);
        assertThat(result.getCitedChunkIds()).containsExactly("chunk-1", "chunk-2");
    }

    @Test
    void createsSynthesisResultWithEmptyCitations() {
        String answerText = "No relevant information found.";
        List<String> citedChunkIds = List.of();

        SynthesisResult result = new SynthesisResult(answerText, citedChunkIds);

        assertThat(result.getAnswerText()).isEqualTo(answerText);
        assertThat(result.getCitedChunkIds()).isEmpty();
    }
}

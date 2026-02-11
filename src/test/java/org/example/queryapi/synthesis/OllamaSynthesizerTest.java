package org.example.queryapi.synthesis;

import org.example.queryapi.domain.Chunk;
import org.example.queryapi.domain.SynthesisResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OllamaSynthesizerTest {

    private ChatModel chatModel;
    private OllamaSynthesizer synthesizer;

    @BeforeEach
    void setUp() {
        chatModel = mock(ChatModel.class);
        synthesizer = new OllamaSynthesizer(chatModel);
    }

    /**
     * Helper method to create mock ChatResponse objects
     */
    private ChatResponse createMockChatResponse(String content) {
        Generation generation = new Generation(new AssistantMessage(content));
        return new ChatResponse(List.of(generation));
    }

    @Test
    void synthesizeWithChunksReturnsAnswerWithCitations() {
        List<Chunk> chunks = List.of(
                new Chunk("chunk-1", "doc-1", "Deployment Guide",
                        "The deployment topology is active-passive.", null, 0.9),
                new Chunk("chunk-2", "doc-2", "Node Config",
                        "Each node runs the same version.", null, 0.85)
        );

        // Mock ChatModel response
        ChatResponse mockResponse = createMockChatResponse(
            "The deployment topology is active-passive [1]. Each node runs the same version [2]."
        );
        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        SynthesisResult result = synthesizer.synthesize("What is the deployment topology?", chunks);

        assertThat(result.getAnswerText()).contains("active-passive");
        assertThat(result.getAnswerText()).contains("[1]");
        assertThat(result.getAnswerText()).contains("[2]");
        assertThat(result.getCitedChunkIds()).containsExactly("chunk-1", "chunk-2");
    }

    @Test
    void synthesizeWithEmptyChunksReturnsNoAnswerMessage() {
        List<Chunk> chunks = List.of();

        SynthesisResult result = synthesizer.synthesize("What is the deployment topology?", chunks);

        assertThat(result.getAnswerText()).contains("No relevant information found");
        assertThat(result.getCitedChunkIds()).isEmpty();
    }

    @Test
    void synthesizeExtractsCitationsFromAnswer() {
        List<Chunk> chunks = List.of(
                new Chunk("chunk-1", "doc-1", "Doc 1", "Content 1", null, 0.9),
                new Chunk("chunk-2", "doc-2", "Doc 2", "Content 2", null, 0.9),
                new Chunk("chunk-3", "doc-3", "Doc 3", "Content 3", null, 0.9)
        );

        // Mock response with citations [1], [2], [3]
        ChatResponse mockResponse = createMockChatResponse("First point [1]. Second point [2]. Third point [3].");
        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        SynthesisResult result = synthesizer.synthesize("Question?", chunks);

        assertThat(result.getCitedChunkIds()).containsExactly("chunk-1", "chunk-2", "chunk-3");
    }

    @Test
    void synthesizeHandlesPartialCitations() {
        List<Chunk> chunks = List.of(
                new Chunk("chunk-1", "doc-1", "Doc 1", "Content 1", null, 0.9),
                new Chunk("chunk-2", "doc-2", "Doc 2", "Content 2", null, 0.9),
                new Chunk("chunk-3", "doc-3", "Doc 3", "Content 3", null, 0.9)
        );

        // Mock response citing only chunks 1 and 3
        ChatResponse mockResponse = createMockChatResponse("First point [1]. Third point [3].");
        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        SynthesisResult result = synthesizer.synthesize("Question?", chunks);

        assertThat(result.getCitedChunkIds()).containsExactly("chunk-1", "chunk-3");
        assertThat(result.getCitedChunkIds()).doesNotContain("chunk-2");
    }

    @Test
    void noCitationsReturnsEmptyList() {
        List<Chunk> chunks = List.of(
                new Chunk("chunk-1", "doc-1", "Doc 1", "Content 1", null, 0.9)
        );

        // Mock response without citations
        ChatResponse mockResponse = createMockChatResponse("This is an answer without citations.");
        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        SynthesisResult result = synthesizer.synthesize("Question?", chunks);

        assertThat(result.getCitedChunkIds()).isEmpty();
    }

    @Test
    void synthesizeThrowsSynthesisExceptionWhenChatModelFails() {
        List<Chunk> chunks = List.of(
                new Chunk("chunk-1", "doc-1", "Doc 1", "Content 1", null, 0.9)
        );

        // Mock ChatModel to throw exception
        when(chatModel.call(any(Prompt.class))).thenThrow(new RuntimeException("LLM error"));

        assertThatThrownBy(() -> synthesizer.synthesize("Question?", chunks))
                .isInstanceOf(SynthesisException.class)
                .hasMessageContaining("Synthesis failed")
                .hasMessageContaining("LLM error");
    }
}

package org.example.queryapi.synthesis;

import org.example.queryapi.domain.Chunk;
import org.example.queryapi.domain.SynthesisResult;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ollama-based implementation of Synthesizer using Spring AI.
 * Uses Spring AI's ChatModel abstraction for Ollama integration.
 */
@Component
public class OllamaSynthesizer implements Synthesizer {

    private static final Pattern CITATION_PATTERN = Pattern.compile("\\[(\\d+)\\]");
    private final ChatModel chatModel;

    public OllamaSynthesizer(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public SynthesisResult synthesize(String query, List<Chunk> chunks) {
        if (chunks == null || chunks.isEmpty()) {
            return new SynthesisResult(
                    "No relevant information found in the knowledge base to answer this question.",
                    List.of()
            );
        }

        try {
            // Build prompt with system and user messages
            Prompt prompt = buildPrompt(query, chunks);

            // Call Ollama via Spring AI ChatModel
            ChatResponse response = chatModel.call(prompt);
            String answer = response.getResult().getOutput().getText();

            // Extract citations from answer
            List<String> citedChunkIds = extractCitedChunkIds(answer, chunks);

            return new SynthesisResult(answer, citedChunkIds);

        } catch (Exception e) {
            throw new SynthesisException("Synthesis failed: " + e.getMessage(), e);
        }
    }

    /**
     * Builds the prompt with system instruction, numbered chunks, and user question.
     * Uses Spring AI's SystemMessage and UserMessage for better structure.
     */
    protected Prompt buildPrompt(String query, List<Chunk> chunks) {
        // System instruction
        String systemInstruction = 
            "You are a helpful assistant that answers questions based on provided context. " +
            "Answer the question using ONLY the information from the numbered chunks below. " +
            "Cite your sources using [1], [2], etc. to reference the chunk numbers. " +
            "If the information is not in the provided chunks, say so.";

        // Build user message with numbered chunks
        StringBuilder userMessage = new StringBuilder();
        userMessage.append("Context Chunks:\n");
        for (int i = 0; i < chunks.size(); i++) {
            Chunk chunk = chunks.get(i);
            userMessage.append(String.format("Chunk %d: %s\n", i + 1, chunk.getText()));
        }
        userMessage.append("\nQuestion: ").append(query);

        return new Prompt(List.of(
            new SystemMessage(systemInstruction),
            new UserMessage(userMessage.toString())
        ));
    }

    /**
     * Extracts citation markers [1], [2], etc. from answer text
     * and maps them to chunk IDs based on chunk order.
     */
    protected List<String> extractCitedChunkIds(String answer, List<Chunk> chunks) {
        List<String> citedChunkIds = new ArrayList<>();
        Matcher matcher = CITATION_PATTERN.matcher(answer);

        while (matcher.find()) {
            String citationNumber = matcher.group(1);
            try {
                int index = Integer.parseInt(citationNumber) - 1; // Convert to 0-based index
                if (index >= 0 && index < chunks.size()) {
                    String chunkId = chunks.get(index).getChunkId();
                    // Add only if not already in list (preserve order)
                    if (!citedChunkIds.contains(chunkId)) {
                        citedChunkIds.add(chunkId);
                    }
                }
            } catch (NumberFormatException e) {
                // Skip invalid citation numbers
            }
        }

        return citedChunkIds;
    }
}

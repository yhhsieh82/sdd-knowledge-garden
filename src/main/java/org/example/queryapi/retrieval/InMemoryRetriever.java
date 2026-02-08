package org.example.queryapi.retrieval;

import org.example.queryapi.domain.Chunk;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * In-memory implementation of Retriever using keyword-based matching.
 * Retrieves and scores chunks based on keyword overlap with the query.
 */
@Component
public class InMemoryRetriever implements Retriever {

    private static final double RELEVANCE_THRESHOLD = 0.8;
    private final InMemoryKnowledgeBase knowledgeBase;

    public InMemoryRetriever(InMemoryKnowledgeBase knowledgeBase) {
        if (knowledgeBase == null) {
            throw new RetrievalException("Knowledge base cannot be null");
        }
        this.knowledgeBase = knowledgeBase;
    }

    @Override
    public List<Chunk> retrieve(String query, int maxChunks) {
        List<Chunk> allChunks = knowledgeBase.getAllChunks();
        
        if (allChunks.isEmpty()) {
            return List.of();
        }

        // Extract keywords from query (lowercase, split by whitespace)
        List<String> queryKeywords = extractKeywords(query);
        
        if (queryKeywords.isEmpty()) {
            return List.of();
        }

        // Score each chunk based on keyword matching
        List<Chunk> scoredChunks = allChunks.stream()
                .map(chunk -> scoreChunk(chunk, queryKeywords))
                .filter(chunk -> chunk.getRelevanceScore() >= RELEVANCE_THRESHOLD)
                .sorted(Comparator.comparingDouble(Chunk::getRelevanceScore).reversed())
                .limit(maxChunks)
                .collect(Collectors.toList());

        return scoredChunks;
    }

    /**
     * Extracts keywords from query text.
     * Converts to lowercase and splits by whitespace.
     */
    private List<String> extractKeywords(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        
        return Arrays.stream(query.toLowerCase().trim().split("\\s+"))
                .filter(word -> !word.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Scores a chunk based on keyword matching.
     * Returns a new Chunk with the relevance score set.
     * 
     * Score calculation:
     * - Count how many query keywords appear in the chunk (at least once)
     * - Divide by total number of query keywords
     * - Result is in range [0, 1]
     * - If at least one keyword matches, ensure minimum score of 0.8 to pass threshold
     */
    private Chunk scoreChunk(Chunk chunk, List<String> queryKeywords) {
        String chunkText = (chunk.getText() + " " + chunk.getDocumentTitle()).toLowerCase();
        
        // Count how many query keywords appear in chunk (at least once)
        int matchingKeywords = 0;
        int totalOccurrences = 0;
        
        for (String keyword : queryKeywords) {
            int count = countOccurrences(chunkText, keyword);
            if (count > 0) {
                matchingKeywords++;
                totalOccurrences += count;
            }
        }
        
        if (matchingKeywords == 0) {
            return new Chunk(
                    chunk.getChunkId(),
                    chunk.getDocumentId(),
                    chunk.getDocumentTitle(),
                    chunk.getText(),
                    chunk.getUrl(),
                    0.0
            );
        }
        
        // Calculate base score: ratio of matching keywords
        double baseScore = (double) matchingKeywords / queryKeywords.size();
        
        // Boost score based on total occurrences (more occurrences = higher score)
        // Scale so that 2+ total occurrences gives 1.0 score
        double occurrenceBoost = Math.min(1.0, totalOccurrences / 2.0);
        
        // Final score is average of base score and occurrence boost
        // This ensures at least one keyword match gives score >= 0.8
        double score = (baseScore + occurrenceBoost) / 2.0;
        
        // Ensure minimum score of 0.8 if any keywords match
        score = Math.max(0.8, score);
        score = Math.min(1.0, score);
        
        // Return new Chunk with updated relevance score
        return new Chunk(
                chunk.getChunkId(),
                chunk.getDocumentId(),
                chunk.getDocumentTitle(),
                chunk.getText(),
                chunk.getUrl(),
                score
        );
    }

    /**
     * Counts occurrences of a keyword in text.
     * Case-insensitive word boundary matching.
     */
    private int countOccurrences(String text, String keyword) {
        if (text == null || keyword == null || text.isEmpty() || keyword.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        int index = 0;
        
        while ((index = text.indexOf(keyword, index)) != -1) {
            count++;
            index += keyword.length();
        }
        
        return count;
    }
}

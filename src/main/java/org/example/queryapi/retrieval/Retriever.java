package org.example.queryapi.retrieval;

import org.example.queryapi.domain.Chunk;

import java.util.List;

/**
 * Interface for retrieving relevant chunks from the knowledge base.
 * Implementations can use different backing stores (in-memory, file-based, vector DB).
 */
public interface Retriever {
    
    /**
     * Retrieves chunks relevant to the query, ordered by relevance (highest first).
     * 
     * @param query the search query
     * @param maxChunks maximum number of chunks to retrieve
     * @return list of chunks ordered by relevance score (descending), empty list if no matches
     * @throws RetrievalException if retrieval fails due to store/network errors
     */
    List<Chunk> retrieve(String query, int maxChunks);
}

package org.example.queryapi.retrieval;

import org.example.queryapi.domain.Chunk;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory storage for knowledge base chunks.
 * Thread-safe implementation suitable for single-node deployment.
 */
@Component
public class InMemoryKnowledgeBase {
    
    private final List<Chunk> chunks;

    public InMemoryKnowledgeBase() {
        this.chunks = new CopyOnWriteArrayList<>();
    }

    /**
     * Adds a chunk to the knowledge base.
     * 
     * @param chunk the chunk to add
     */
    public void addChunk(Chunk chunk) {
        chunks.add(chunk);
    }

    /**
     * Gets all chunks from the knowledge base.
     * 
     * @return list of all chunks
     */
    public List<Chunk> getAllChunks() {
        return new ArrayList<>(chunks);
    }

    /**
     * Clears all chunks from the knowledge base.
     */
    public void clear() {
        chunks.clear();
    }

    /**
     * Gets the number of chunks in the knowledge base.
     * 
     * @return chunk count
     */
    public int size() {
        return chunks.size();
    }
}

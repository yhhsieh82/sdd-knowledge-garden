package org.example.queryapi.domain;

/**
 * Domain model representing a retrieved chunk from the knowledge base.
 * Contains chunk metadata and relevance scoring for retrieval.
 */
public class Chunk {
    private final String chunkId;
    private final String documentId;
    private final String documentTitle;
    private final String text;
    private final String url;  // optional
    private final double relevanceScore;

    public Chunk(String chunkId, String documentId, String documentTitle, 
                 String text, String url, double relevanceScore) {
        this.chunkId = chunkId;
        this.documentId = documentId;
        this.documentTitle = documentTitle;
        this.text = text;
        this.url = url;
        this.relevanceScore = relevanceScore;
    }

    public String getChunkId() {
        return chunkId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }

    public double getRelevanceScore() {
        return relevanceScore;
    }
}

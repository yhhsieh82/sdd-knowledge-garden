package org.example.queryapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO representing a cited source document.
 * One entry per source document (deduplicates multiple chunks from same document).
 */
@Schema(description = "Cited source document with metadata")
public class CitedDocument {

    @Schema(description = "Document identifier", example = "doc-1")
    private String id;

    @Schema(description = "Document title", example = "Deployment Guide")
    private String title;

    @Schema(description = "Chunk text or summary", example = "The recommended deployment topology...")
    private String snippet;

    @Schema(description = "Optional document URL", example = "https://example.com/docs/deployment")
    private String url;

    public CitedDocument() {
    }

    public CitedDocument(String id, String title, String snippet, String url) {
        this.id = id;
        this.title = title;
        this.snippet = snippet;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

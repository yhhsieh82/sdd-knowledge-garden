package org.example.queryapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.queryapi.domain.Chunk;
import org.example.queryapi.domain.SynthesisResult;
import org.example.queryapi.dto.CitedDocument;
import org.example.queryapi.dto.ErrorResponse;
import org.example.queryapi.dto.QueryRequest;
import org.example.queryapi.dto.QueryResponse;
import org.example.queryapi.dto.ResponseMetadata;
import org.example.queryapi.retrieval.RetrievalException;
import org.example.queryapi.retrieval.Retriever;
import org.example.queryapi.synthesis.SynthesisException;
import org.example.queryapi.synthesis.Synthesizer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for Query API operations.
 * Provides endpoint for submitting natural-language queries against the knowledge base.
 */
@RestController
@Tag(name = "Query API", description = "Natural-language query operations for RAG knowledge garden")
public class QueryController {

    private static final int DEFAULT_MAX_SOURCES = 10;

    private final Retriever retriever;
    private final Synthesizer synthesizer;

    public QueryController(Retriever retriever, Synthesizer synthesizer) {
        this.retriever = retriever;
        this.synthesizer = synthesizer;
    }

    @PostMapping("/query")
    @Operation(
            summary = "Submit a natural-language query",
            description = "Retrieves relevant knowledge base chunks and synthesizes an answer with citations",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Query processed successfully",
                            content = @Content(schema = @Schema(implementation = QueryResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Service unavailable (retrieval or synthesis failed)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<QueryResponse> query(@Valid @RequestBody QueryRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            // Determine maxSources (use provided value or default)
            int maxSources = request.getMaxSources() != null
                    ? request.getMaxSources()
                    : DEFAULT_MAX_SOURCES;

            // Step 1: Retrieve relevant chunks
            List<Chunk> chunks = retriever.retrieve(request.getQuery(), maxSources);

            // Step 2: Handle empty retrieval
            if (chunks.isEmpty()) {
                long processingTime = System.currentTimeMillis() - startTime;
                QueryResponse response = new QueryResponse(
                        "No relevant information found in the knowledge base to answer this question.",
                        false,
                        List.of(),
                        new ResponseMetadata(0, 0, processingTime)
                );
                return ResponseEntity.ok(response);
            }

            // Step 3: Synthesize answer
            SynthesisResult synthesisResult = synthesizer.synthesize(request.getQuery(), chunks);

            // Step 4: Build cited documents list with deduplication
            List<CitedDocument> citedDocuments = buildCitedDocuments(synthesisResult, chunks);

            // Step 5: Build metadata
            long processingTime = System.currentTimeMillis() - startTime;
            ResponseMetadata metadata = new ResponseMetadata(
                    chunks.size(),
                    citedDocuments.size(),
                    processingTime
            );

            // Step 6: Build response
            QueryResponse response = new QueryResponse(
                    synthesisResult.getAnswerText(),
                    true,
                    citedDocuments,
                    metadata
            );

            return ResponseEntity.ok(response);

        } catch (RetrievalException e) {
            throw e; // Will be handled by @ExceptionHandler
        } catch (SynthesisException e) {
            throw e; // Will be handled by @ExceptionHandler
        }
    }

    /**
     * Build cited documents list from synthesis result and chunks, with deduplication.
     */
    private List<CitedDocument> buildCitedDocuments(SynthesisResult synthesisResult, List<Chunk> chunks) {
        // Create map of chunkId -> Chunk for quick lookup
        Map<String, Chunk> chunkMap = chunks.stream()
                .collect(Collectors.toMap(Chunk::getChunkId, chunk -> chunk));

        // Track seen documentIds for deduplication
        Set<String> seenDocumentIds = new LinkedHashSet<>();
        List<CitedDocument> citedDocuments = new ArrayList<>();

        // Process cited chunk IDs in order
        for (String chunkId : synthesisResult.getCitedChunkIds()) {
            Chunk chunk = chunkMap.get(chunkId);
            if (chunk != null && !seenDocumentIds.contains(chunk.getDocumentId())) {
                seenDocumentIds.add(chunk.getDocumentId());
                citedDocuments.add(new CitedDocument(
                        chunk.getDocumentId(),
                        chunk.getDocumentTitle(),
                        chunk.getText(), // Use chunk text as snippet
                        chunk.getUrl()
                ));
            }
        }

        return citedDocuments;
    }

    /**
     * Handle validation errors (Bean Validation failures).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        // Collect all validation errors
        Map<String, Object> details = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.put(error.getField(), error.getDefaultMessage());
        }

        return new ErrorResponse(
                "VALIDATION_ERROR",
                "Request validation failed: " + ex.getBindingResult().getErrorCount() + " error(s)",
                details
        );
    }

    /**
     * Handle retrieval failures.
     */
    @ExceptionHandler(RetrievalException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleRetrievalException(RetrievalException ex) {
        return new ErrorResponse(
                "RETRIEVAL_FAILED",
                "Failed to retrieve knowledge base content: " + ex.getMessage(),
                null
        );
    }

    /**
     * Handle synthesis failures.
     */
    @ExceptionHandler(SynthesisException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleSynthesisException(SynthesisException ex) {
        return new ErrorResponse(
                "SYNTHESIS_FAILED",
                "Failed to synthesize answer: " + ex.getMessage(),
                null
        );
    }
}

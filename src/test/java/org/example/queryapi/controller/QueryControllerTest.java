package org.example.queryapi.controller;

import org.example.queryapi.domain.Chunk;
import org.example.queryapi.domain.SynthesisResult;
import org.example.queryapi.dto.QueryRequest;
import org.example.queryapi.dto.QueryResponse;
import org.example.queryapi.retrieval.RetrievalException;
import org.example.queryapi.retrieval.Retriever;
import org.example.queryapi.synthesis.SynthesisException;
import org.example.queryapi.synthesis.Synthesizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QueryController.class)
class QueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Retriever retriever;

    @MockBean
    private Synthesizer synthesizer;

    @Test
    void postQueryEndpointAcceptsValidRequest() throws Exception {
        // Setup test data
        List<Chunk> chunks = List.of(
                new Chunk("chunk-1", "doc-1", "Deployment Guide",
                        "The deployment topology is active-passive.", "http://docs.example.com/deploy", 0.9)
        );
        SynthesisResult synthesisResult = new SynthesisResult(
                "The deployment topology is active-passive [1].",
                List.of("chunk-1")
        );

        when(retriever.retrieve(anyString(), anyInt())).thenReturn(chunks);
        when(synthesizer.synthesize(anyString(), any())).thenReturn(synthesisResult);

        String requestJson = """
                {
                    "query": "What is the deployment topology?",
                    "maxSources": 5
                }
                """;

        mockMvc.perform(post("/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value(containsString("active-passive")))
                .andExpect(jsonPath("$.answerSynthesized").value(true))
                .andExpect(jsonPath("$.citedDocuments", hasSize(1)))
                .andExpect(jsonPath("$.citedDocuments[0].id").value("doc-1"))
                .andExpect(jsonPath("$.citedDocuments[0].title").value("Deployment Guide"))
                .andExpect(jsonPath("$.citedDocuments[0].url").value("http://docs.example.com/deploy"))
                .andExpect(jsonPath("$.metadata.totalChunksRetrieved").value(1));
    }

    @Test
    void validationFailureReturns400() throws Exception {
        String requestJson = """
                {
                    "query": "",
                    "maxSources": 5
                }
                """;

        mockMvc.perform(post("/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void emptyRetrievalReturns200WithAnswerSynthesizedFalse() throws Exception {
        when(retriever.retrieve(anyString(), anyInt())).thenReturn(List.of());

        String requestJson = """
                {
                    "query": "What is the deployment topology?"
                }
                """;

        mockMvc.perform(post("/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value(containsString("No relevant information found")))
                .andExpect(jsonPath("$.answerSynthesized").value(false))
                .andExpect(jsonPath("$.citedDocuments", hasSize(0)))
                .andExpect(jsonPath("$.metadata.totalChunksRetrieved").value(0));
    }

    @Test
    void retrievalExceptionReturns503() throws Exception {
        when(retriever.retrieve(anyString(), anyInt()))
                .thenThrow(new RetrievalException("Database connection failed"));

        String requestJson = """
                {
                    "query": "What is the deployment topology?"
                }
                """;

        mockMvc.perform(post("/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("RETRIEVAL_FAILED"))
                .andExpect(jsonPath("$.message").value(containsString("Database connection failed")));
    }

    @Test
    void synthesisExceptionReturns503() throws Exception {
        List<Chunk> chunks = List.of(
                new Chunk("chunk-1", "doc-1", "Deployment Guide",
                        "The deployment topology is active-passive.", null, 0.9)
        );
        when(retriever.retrieve(anyString(), anyInt())).thenReturn(chunks);
        when(synthesizer.synthesize(anyString(), any()))
                .thenThrow(new SynthesisException("LLM timeout"));

        String requestJson = """
                {
                    "query": "What is the deployment topology?"
                }
                """;

        mockMvc.perform(post("/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("SYNTHESIS_FAILED"))
                .andExpect(jsonPath("$.message").value(containsString("LLM timeout")));
    }

    @Test
    void citationDeduplicationWorks() throws Exception {
        // Multiple chunks from same document
        List<Chunk> chunks = List.of(
                new Chunk("chunk-1", "doc-1", "Deployment Guide",
                        "The deployment topology is active-passive.", "http://docs.example.com/deploy", 0.9),
                new Chunk("chunk-2", "doc-1", "Deployment Guide",
                        "Each node runs the same version.", "http://docs.example.com/deploy", 0.85),
                new Chunk("chunk-3", "doc-2", "Node Config",
                        "Configure the node settings.", "http://docs.example.com/node", 0.8)
        );
        // Citations reference chunks 1, 2, and 3
        SynthesisResult synthesisResult = new SynthesisResult(
                "Answer with citations [1], [2], and [3].",
                List.of("chunk-1", "chunk-2", "chunk-3")
        );

        when(retriever.retrieve(anyString(), anyInt())).thenReturn(chunks);
        when(synthesizer.synthesize(anyString(), any())).thenReturn(synthesisResult);

        String requestJson = """
                {
                    "query": "What is the deployment topology?"
                }
                """;

        mockMvc.perform(post("/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.citedDocuments", hasSize(2))) // Only 2 unique documents
                .andExpect(jsonPath("$.citedDocuments[0].id").value("doc-1"))
                .andExpect(jsonPath("$.citedDocuments[1].id").value("doc-2"));
    }

    @Test
    void usesDefaultMaxSourcesWhenNotProvided() throws Exception {
        List<Chunk> chunks = List.of(
                new Chunk("chunk-1", "doc-1", "Doc", "Content", null, 0.9)
        );
        SynthesisResult synthesisResult = new SynthesisResult("Answer [1].", List.of("chunk-1"));

        when(retriever.retrieve(anyString(), anyInt())).thenReturn(chunks);
        when(synthesizer.synthesize(anyString(), any())).thenReturn(synthesisResult);

        String requestJson = """
                {
                    "query": "What is the topology?"
                }
                """;

        mockMvc.perform(post("/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());

        // Verify default maxSources (10) was used
        org.mockito.Mockito.verify(retriever).retrieve(anyString(), org.mockito.Mockito.eq(10));
    }

    @Test
    void metadataIncludesAllRequiredFields() throws Exception {
        List<Chunk> chunks = List.of(
                new Chunk("chunk-1", "doc-1", "Doc", "Content", null, 0.9),
                new Chunk("chunk-2", "doc-2", "Doc", "Content", null, 0.85)
        );
        SynthesisResult synthesisResult = new SynthesisResult("Answer [1].", List.of("chunk-1"));

        when(retriever.retrieve(anyString(), anyInt())).thenReturn(chunks);
        when(synthesizer.synthesize(anyString(), any())).thenReturn(synthesisResult);

        String requestJson = """
                {
                    "query": "What is the topology?"
                }
                """;

        mockMvc.perform(post("/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.totalChunksRetrieved").value(2))
                .andExpect(jsonPath("$.metadata.totalDocumentsCited").value(1))
                .andExpect(jsonPath("$.metadata.processingTimeMs").isNumber())
                .andExpect(jsonPath("$.metadata.processingTimeMs").value(greaterThanOrEqualTo(0)));
    }
}

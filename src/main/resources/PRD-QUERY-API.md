# Product Requirements Document: Query API

**Document type:** Product Requirements Document (PRD)  
**Source:** Reverse-generated from current implementation  
**Scope:** Query capability (RAG Knowledge Garden) — the ability for users to ask questions and get cited answers.

---

## 1. Problem & Context

Users need to get answers from a curated knowledge base (the “knowledge garden”) in a way that is **traceable**: they should see which source documents support the answer. A single API entry point should accept a natural-language question and return an answer grounded in retrieved content, with clear attribution to sources.

---

## 2. Users

- **Primary:** Any client (e.g. frontend app, integration, script) that needs to query the knowledge garden programmatically.
- **Indirect:** End users who consume the answers and citations delivered through that client.

---

## 3. Goals

- **Answer questions** from the knowledge base using natural language.
- **Ground answers in retrieval** so responses are based on actual stored content, not general knowledge.
- **Provide traceability** by returning which documents were used and how they map to citations in the answer (e.g. `[1]`, `[2]`).
- **Expose a simple contract:** one request (the question), one response (answer + sources + metadata).

---

## 4. Success Criteria

- A client can submit a question and receive a structured JSON response that includes:
  - A natural-language answer, with inline citations where applicable.
  - The set of documents that were cited in the answer.
  - Basic execution metadata (e.g. processing time, whether an answer was synthesized).
- Invalid or missing input (e.g. blank or over-length query) is rejected with clear validation behavior.
- The system follows a consistent flow: validate input → retrieve relevant chunks → synthesize answer with citations → return response.

---

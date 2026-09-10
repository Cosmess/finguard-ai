package com.cosmess.finguard.knowledge.application;

import java.util.Objects;
import java.util.UUID;

public record KnowledgeSearchResult(
        UUID documentId,
        String title,
        double score,
        String excerpt,
        Citation citation
) {

    public KnowledgeSearchResult {
        Objects.requireNonNull(documentId, "documentId is required");
        Objects.requireNonNull(title, "title is required");
        Objects.requireNonNull(excerpt, "excerpt is required");
        Objects.requireNonNull(citation, "citation is required");
        if (score <= 0) {
            throw new IllegalArgumentException("score must be positive");
        }
    }

    public record Citation(UUID documentId, String sourceUri, String title) {

        public Citation {
            Objects.requireNonNull(documentId, "documentId is required");
            Objects.requireNonNull(sourceUri, "sourceUri is required");
            Objects.requireNonNull(title, "title is required");
        }
    }
}
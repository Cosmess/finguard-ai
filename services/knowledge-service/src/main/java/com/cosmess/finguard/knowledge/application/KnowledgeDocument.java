package com.cosmess.finguard.knowledge.application;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record KnowledgeDocument(
        UUID id,
        String title,
        String sourceUri,
        String content,
        Instant createdAt
) {

    public KnowledgeDocument {
        Objects.requireNonNull(id, "id is required");
        requireText(title, "title");
        requireText(sourceUri, "sourceUri");
        requireText(content, "content");
        Objects.requireNonNull(createdAt, "createdAt is required");
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }
}
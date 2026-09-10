package com.cosmess.finguard.knowledge.adapter.out.persistence;

import com.cosmess.finguard.knowledge.application.KnowledgeDocument;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "knowledge_documents")
public class KnowledgeDocumentEntity {

    @Id
    private UUID id;
    @Column(nullable = false, length = 200)
    private String title;
    @Column(nullable = false, length = 500)
    private String sourceUri;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(nullable = false)
    private Instant createdAt;

    protected KnowledgeDocumentEntity() {
    }

    private KnowledgeDocumentEntity(KnowledgeDocument document) {
        this.id = document.id();
        this.title = document.title();
        this.sourceUri = document.sourceUri();
        this.content = document.content();
        this.createdAt = document.createdAt();
    }

    public static KnowledgeDocumentEntity from(KnowledgeDocument document) {
        return new KnowledgeDocumentEntity(document);
    }

    public KnowledgeDocument toDocument() {
        return new KnowledgeDocument(id, title, sourceUri, content, createdAt);
    }
}
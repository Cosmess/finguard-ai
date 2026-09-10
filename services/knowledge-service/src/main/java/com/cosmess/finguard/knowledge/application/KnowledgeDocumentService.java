package com.cosmess.finguard.knowledge.application;

import com.cosmess.finguard.knowledge.adapter.out.persistence.KnowledgeDocumentEntity;
import com.cosmess.finguard.knowledge.adapter.out.persistence.KnowledgeDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class KnowledgeDocumentService {

    private static final Pattern WORD_SEPARATOR = Pattern.compile("[^\\p{L}\\p{N}]+");
    private final KnowledgeDocumentRepository repository;
    private final Clock clock;

    @Autowired
    public KnowledgeDocumentService(KnowledgeDocumentRepository repository) {
        this(repository, Clock.systemUTC());
    }

    KnowledgeDocumentService(KnowledgeDocumentRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public KnowledgeDocument ingest(String title, String sourceUri, String content) {
        KnowledgeDocument document = new KnowledgeDocument(
                UUID.randomUUID(),
                title,
                sourceUri,
                content,
                clock.instant()
        );
        return repository.save(KnowledgeDocumentEntity.from(document)).toDocument();
    }

    @Transactional(readOnly = true)
    public List<KnowledgeSearchResult> search(String query, int limit) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("query is required");
        }
        if (limit < 1 || limit > 20) {
            throw new IllegalArgumentException("limit must be between 1 and 20");
        }

        List<String> terms = terms(query);
        return repository.findAll().stream()
                .map(KnowledgeDocumentEntity::toDocument)
                .map(document -> result(document, terms))
                .filter(result -> result != null)
                .sorted(Comparator.comparingDouble(KnowledgeSearchResult::score).reversed())
                .limit(limit)
                .toList();
    }

    private KnowledgeSearchResult result(KnowledgeDocument document, List<String> terms) {
        String normalizedContent = document.content().toLowerCase(Locale.ROOT);
        long matches = terms.stream().filter(normalizedContent::contains).count();
        if (matches == 0) {
            return null;
        }

        int firstMatch = terms.stream()
                .mapToInt(normalizedContent::indexOf)
                .filter(index -> index >= 0)
                .min()
                .orElse(0);
        int start = Math.max(0, firstMatch - 80);
        int end = Math.min(document.content().length(), firstMatch + 180);
        String excerpt = document.content().substring(start, end).trim();
        return new KnowledgeSearchResult(
                document.id(),
                document.title(),
                (double) matches / terms.size(),
                excerpt,
                new KnowledgeSearchResult.Citation(document.id(), document.sourceUri(), document.title())
        );
    }

    private List<String> terms(String query) {
        return WORD_SEPARATOR.splitAsStream(query.toLowerCase(Locale.ROOT))
                .filter(term -> term.length() > 1)
                .distinct()
                .toList();
    }
}
package com.cosmess.finguard.knowledge.application;

import com.cosmess.finguard.knowledge.adapter.out.persistence.KnowledgeDocumentEntity;
import com.cosmess.finguard.knowledge.adapter.out.persistence.KnowledgeDocumentRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KnowledgeDocumentServiceTest {

    private final KnowledgeDocumentRepository repository = mock(KnowledgeDocumentRepository.class);
    private final KnowledgeDocumentService service = new KnowledgeDocumentService(
            repository,
            Clock.fixed(Instant.parse("2026-09-10T12:00:00Z"), ZoneOffset.UTC)
    );

    @Test
    void ranksDocumentsAndIncludesCitationForMatchingContent() {
        KnowledgeDocument first = document("fraud-policy", "Policy on velocity signals and manual review.");
        KnowledgeDocument second = document("payment-policy", "Payment retention policy.");
        when(repository.findAll()).thenReturn(List.of(
                KnowledgeDocumentEntity.from(first),
                KnowledgeDocumentEntity.from(second)
        ));

        List<KnowledgeSearchResult> results = service.search("velocity review", 5);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().title()).isEqualTo("fraud-policy");
        assertThat(results.getFirst().citation().sourceUri()).isEqualTo("s3://policy/fraud-policy");
        assertThat(results.getFirst().excerpt()).contains("velocity");
    }

    @Test
    void rejectsInvalidSearchLimit() {
        assertThatThrownBy(() -> service.search("fraud", 21))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("limit must be between 1 and 20");
    }

    private KnowledgeDocument document(String title, String content) {
        return new KnowledgeDocument(
                UUID.randomUUID(),
                title,
                "s3://policy/" + title,
                content,
                Instant.parse("2026-09-10T11:00:00Z")
        );
    }
}
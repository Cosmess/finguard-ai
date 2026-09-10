package com.cosmess.finguard.dispute.application;

import com.cosmess.finguard.dispute.adapter.out.persistence.DisputeEntity;
import com.cosmess.finguard.dispute.adapter.out.persistence.DisputeRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DisputeServiceTest {

    private final DisputeRepository repository = mock(DisputeRepository.class);
    private final DisputeService service = new DisputeService(repository, Clock.fixed(Instant.parse("2026-09-10T12:00:00Z"), ZoneOffset.UTC));

    @Test
    void opensAddsEvidenceAndResolvesWithHumanReview() {
        when(repository.save(any(DisputeEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Dispute opened = service.open(UUID.randomUUID(), "customer-1", "Unauthorized transaction");
        when(repository.findById(opened.id())).thenReturn(java.util.Optional.of(DisputeEntity.from(opened)));

        Dispute withEvidence = service.addEvidence(opened.id(), "RECEIPT", "s3://evidence/1", "Receipt supplied by analyst");
        when(repository.findById(opened.id())).thenReturn(java.util.Optional.of(DisputeEntity.from(withEvidence)));

        Dispute resolved = service.review(opened.id(), "analyst-1", ReviewDecision.APPROVE);

        assertThat(resolved.status()).isEqualTo(DisputeStatus.RESOLVED);
        assertThat(resolved.evidence()).hasSize(1);
        assertThat(resolved.reviewerId()).isEqualTo("analyst-1");
    }

    @Test
    void rejectsReviewBeforeEvidenceReviewState() {
        Dispute opened = new Dispute(UUID.randomUUID(), UUID.randomUUID(), "customer-1", "Duplicate charge", DisputeStatus.OPEN, null, null, Instant.now(), Instant.now(), List.of());
        when(repository.findById(opened.id())).thenReturn(java.util.Optional.of(DisputeEntity.from(opened)));

        assertThatThrownBy(() -> service.review(opened.id(), "analyst-1", ReviewDecision.REJECT))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Dispute must be under review");
    }
}
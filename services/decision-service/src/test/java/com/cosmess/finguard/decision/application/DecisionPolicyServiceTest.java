package com.cosmess.finguard.decision.application;

import com.cosmess.finguard.decision.adapter.out.persistence.DecisionEntity;
import com.cosmess.finguard.decision.adapter.out.persistence.DecisionRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DecisionPolicyServiceTest {

    private final DecisionRepository repository = mock(DecisionRepository.class);
    private final DecisionPolicyService service = new DecisionPolicyService(repository, Clock.fixed(Instant.parse("2026-09-10T12:00:00Z"), ZoneOffset.UTC));

    @Test
    void holdsHighRiskTransactionForHumanReview() {
        when(repository.save(any(DecisionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DecisionResult result = service.decide(new DecisionRequest(UUID.randomUUID(), 85, "HIGH", "REVIEW_MANUALLY", null));

        assertThat(result.outcome()).isEqualTo(DecisionOutcome.HOLD_FOR_REVIEW);
        assertThat(result.signals()).containsExactly("HIGH_FRAUD_RISK", "AI_RECOMMENDS_REVIEW");
    }

    @Test
    void approvesWhenNoEscalationSignalExists() {
        when(repository.save(any(DecisionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DecisionResult result = service.decide(new DecisionRequest(UUID.randomUUID(), 10, "LOW", "NO_ACTION", null));

        assertThat(result.outcome()).isEqualTo(DecisionOutcome.APPROVE);
    }
}
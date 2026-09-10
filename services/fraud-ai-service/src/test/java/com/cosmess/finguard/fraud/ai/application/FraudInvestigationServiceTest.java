package com.cosmess.finguard.fraud.ai.application;

import com.cosmess.finguard.events.fraud.FraudSuspectedEvent;
import com.cosmess.finguard.fraud.ai.adapter.out.persistence.FraudInvestigationAuditRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class FraudInvestigationServiceTest {

    private static final Instant GENERATED_AT = Instant.parse("2026-09-10T12:00:00Z");

    private final FraudInvestigationAuditRepository auditRepository = mock(FraudInvestigationAuditRepository.class);
    private final FraudInvestigationService service = new FraudInvestigationService(
            auditRepository,
            Clock.fixed(GENERATED_AT, ZoneOffset.UTC)
    );

    @Test
    void createsStructuredManualReviewRecommendationForHighRisk() {
        FraudInvestigationRecommendation recommendation = service.investigate(event("HIGH"));

        assertThat(recommendation.recommendedAction()).isEqualTo("REVIEW_MANUALLY");
        assertThat(recommendation.rationale()).contains("human review");
        assertThat(recommendation.generatedAt()).isEqualTo(GENERATED_AT);
        verify(auditRepository).save(ArgumentCaptor.forClass(com.cosmess.finguard.fraud.ai.adapter.out.persistence.FraudInvestigationAuditEntity.class).capture());
    }

    @Test
    void createsReadOnlyContextRecommendationForMediumRisk() {
        FraudInvestigationRecommendation recommendation = service.investigate(event("MEDIUM"));

        assertThat(recommendation.recommendedAction()).isEqualTo("COLLECT_MORE_CONTEXT");
    }

    @Test
    void rejectsUnsupportedRiskLevelsWithoutAuditing() {
        assertThatThrownBy(() -> service.investigate(event("UNKNOWN")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsupported fraud risk level: UNKNOWN");
    }

    private FraudSuspectedEvent event(String riskLevel) {
        return new FraudSuspectedEvent(
                UUID.randomUUID(),
                "customer-1",
                BigDecimal.valueOf(1250),
                "BRL",
                80,
                riskLevel,
                List.of("HIGH_VALUE"),
                Instant.parse("2026-09-10T11:59:00Z")
        );
    }
}
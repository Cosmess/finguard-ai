package com.cosmess.finguard.fraud.detection.domain;

import com.cosmess.finguard.events.payment.TransactionCreatedEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FraudRulesEngineTest {

    private final FraudRulesEngine engine = new FraudRulesEngine();

    @Test
    void classifiesHighRiskWhenHighValueNewDeviceAndUnusualHourMatch() {
        FraudAssessment assessment = engine.assess(transaction(new BigDecimal("7500.00"), "new-device-1", "2026-09-10T02:30:00Z"));

        assertThat(assessment.score()).isEqualTo(90);
        assertThat(assessment.riskLevel()).isEqualTo(RiskLevel.HIGH);
        assertThat(assessment.triggeredRules()).containsExactly("HIGH_VALUE", "NEW_DEVICE", "UNUSUAL_HOUR");
    }

    @Test
    void classifiesMediumRiskWhenOnlyNewDeviceMatches() {
        FraudAssessment assessment = engine.assess(transaction(new BigDecimal("100.00"), "new-device-1", "2026-09-10T14:00:00Z"));

        assertThat(assessment.score()).isEqualTo(25);
        assertThat(assessment.riskLevel()).isEqualTo(RiskLevel.MEDIUM);
        assertThat(assessment.triggeredRules()).containsExactly("NEW_DEVICE");
    }

    @Test
    void classifiesLowRiskWhenNoRuleMatches() {
        FraudAssessment assessment = engine.assess(transaction(new BigDecimal("100.00"), "known-device-1", "2026-09-10T14:00:00Z"));

        assertThat(assessment.score()).isZero();
        assertThat(assessment.riskLevel()).isEqualTo(RiskLevel.LOW);
        assertThat(assessment.triggeredRules()).isEmpty();
    }

    private TransactionCreatedEvent transaction(BigDecimal amount, String deviceId, String createdAt) {
        return new TransactionCreatedEvent(
                UUID.randomUUID(),
                "merchant-1",
                "customer-1",
                amount,
                "BRL",
                "CREDIT_CARD",
                deviceId,
                "127.0.0.1",
                "CREATED",
                Instant.parse(createdAt)
        );
    }
}

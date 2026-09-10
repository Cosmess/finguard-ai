package com.cosmess.finguard.fraud.detection.adapter.out.persistence;

import com.cosmess.finguard.fraud.detection.application.FraudCase;
import com.cosmess.finguard.fraud.detection.domain.RiskLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "fraud_cases")
class FraudCaseEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID transactionId;

    @Column(nullable = false, length = 80)
    private String customerId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private int score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskLevel riskLevel;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String triggeredRules;

    @Column(nullable = false)
    private Instant createdAt;

    protected FraudCaseEntity() {
    }

    private FraudCaseEntity(FraudCase fraudCase) {
        this.id = fraudCase.id();
        this.transactionId = fraudCase.transactionId();
        this.customerId = fraudCase.customerId();
        this.amount = fraudCase.amount();
        this.currency = fraudCase.currency();
        this.score = fraudCase.score();
        this.riskLevel = fraudCase.riskLevel();
        this.triggeredRules = String.join(",", fraudCase.triggeredRules());
        this.createdAt = fraudCase.createdAt();
    }

    static FraudCaseEntity fromDomain(FraudCase fraudCase) {
        return new FraudCaseEntity(fraudCase);
    }

    FraudCase toDomain() {
        List<String> rules = triggeredRules == null || triggeredRules.isBlank()
                ? List.of()
                : Arrays.asList(triggeredRules.split(","));

        return new FraudCase(id, transactionId, customerId, amount, currency, score, riskLevel, rules, createdAt);
    }
}

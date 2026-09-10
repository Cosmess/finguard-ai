package com.cosmess.finguard.fraud.ai.application;

import com.cosmess.finguard.events.fraud.FraudSuspectedEvent;
import com.cosmess.finguard.fraud.ai.adapter.out.persistence.FraudInvestigationAuditEntity;
import com.cosmess.finguard.fraud.ai.adapter.out.persistence.FraudInvestigationAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class FraudInvestigationService {

    private final FraudInvestigationAuditRepository auditRepository;
    private final Clock clock;

    @Autowired
    public FraudInvestigationService(FraudInvestigationAuditRepository auditRepository) {
        this(auditRepository, Clock.systemUTC());
    }

    FraudInvestigationService(FraudInvestigationAuditRepository auditRepository, Clock clock) {
        this.auditRepository = auditRepository;
        this.clock = clock;
    }

    @Transactional
    public FraudInvestigationRecommendation investigate(FraudSuspectedEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Fraud event is required");
        }

        FraudInvestigationRecommendation recommendation = switch (event.riskLevel()) {
            case "HIGH" -> recommendation(event, "REVIEW_MANUALLY", "High-risk transaction requires human review before any financial action.");
            case "MEDIUM" -> recommendation(event, "COLLECT_MORE_CONTEXT", "Medium-risk transaction requires additional read-only context.");
            case "LOW" -> recommendation(event, "NO_ACTION", "Low-risk transaction has no recommendation for escalation.");
            default -> throw new IllegalArgumentException("Unsupported fraud risk level: " + event.riskLevel());
        };

        auditRepository.save(FraudInvestigationAuditEntity.from(recommendation));
        return recommendation;
    }

    private FraudInvestigationRecommendation recommendation(
            FraudSuspectedEvent event,
            String recommendedAction,
            String rationale
    ) {
        Instant generatedAt = clock.instant();
        return new FraudInvestigationRecommendation(
                event.transactionId(),
                event.riskLevel(),
                recommendedAction,
                rationale,
                event.triggeredRules(),
                generatedAt
        );
    }
}
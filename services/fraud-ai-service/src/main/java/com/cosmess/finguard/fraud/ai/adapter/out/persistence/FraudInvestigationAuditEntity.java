package com.cosmess.finguard.fraud.ai.adapter.out.persistence;

import com.cosmess.finguard.fraud.ai.application.FraudInvestigationRecommendation;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "fraud_investigation_audits")
public class FraudInvestigationAuditEntity {

    @Id
    private UUID transactionId;

    private String riskLevel;
    private String recommendedAction;
    private String rationale;
    private Instant generatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "fraud_investigation_evidence",
            joinColumns = @JoinColumn(name = "transaction_id")
    )
    private List<String> evidence = new ArrayList<>();

    protected FraudInvestigationAuditEntity() {
    }

    private FraudInvestigationAuditEntity(FraudInvestigationRecommendation recommendation) {
        this.transactionId = recommendation.transactionId();
        this.riskLevel = recommendation.riskLevel();
        this.recommendedAction = recommendation.recommendedAction();
        this.rationale = recommendation.rationale();
        this.generatedAt = recommendation.generatedAt();
        this.evidence = new ArrayList<>(recommendation.evidence());
    }

    public static FraudInvestigationAuditEntity from(FraudInvestigationRecommendation recommendation) {
        return new FraudInvestigationAuditEntity(recommendation);
    }
}
package com.cosmess.finguard.decision.application;

import com.cosmess.finguard.decision.adapter.out.persistence.DecisionEntity;
import com.cosmess.finguard.decision.adapter.out.persistence.DecisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DecisionPolicyService {

    private final DecisionRepository repository;
    private final Clock clock;

    @Autowired
    public DecisionPolicyService(DecisionRepository repository) {
        this(repository, Clock.systemUTC());
    }

    DecisionPolicyService(DecisionRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public DecisionResult decide(DecisionRequest request) {
        List<String> signals = new ArrayList<>();
        if (request.fraudScore() >= 70 || "HIGH".equals(request.fraudRiskLevel())) {
            signals.add("HIGH_FRAUD_RISK");
        }
        if ("REVIEW_MANUALLY".equals(request.aiRecommendation())) {
            signals.add("AI_RECOMMENDS_REVIEW");
        }
        if (request.disputeStatus() != null && !request.disputeStatus().isBlank()) {
            signals.add("DISPUTE_" + request.disputeStatus());
        }

        DecisionOutcome outcome;
        String rationale;
        if (signals.contains("HIGH_FRAUD_RISK") || signals.contains("AI_RECOMMENDS_REVIEW")) {
            outcome = DecisionOutcome.HOLD_FOR_REVIEW;
            rationale = "Risk signals require human review before a financial decision.";
        } else {
            outcome = DecisionOutcome.APPROVE;
            rationale = "No configured signal requires escalation.";
        }

        DecisionResult result = new DecisionResult(UUID.randomUUID(), request.transactionId(), outcome, rationale, signals, clock.instant());
        return repository.save(DecisionEntity.from(result)).toResult();
    }
}
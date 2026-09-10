package com.cosmess.finguard.decision.adapter.out.persistence;

import com.cosmess.finguard.decision.application.DecisionOutcome;
import com.cosmess.finguard.decision.application.DecisionResult;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "decisions")
public class DecisionEntity {

    @Id
    private UUID decisionId;
    private UUID transactionId;
    @Enumerated(EnumType.STRING)
    private DecisionOutcome outcome;
    private String rationale;
    private Instant decidedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "decision_signals", joinColumns = @JoinColumn(name = "decision_id"))
    private List<String> signals = new ArrayList<>();

    protected DecisionEntity() {
    }

    private DecisionEntity(DecisionResult result) {
        this.decisionId = result.decisionId();
        this.transactionId = result.transactionId();
        this.outcome = result.outcome();
        this.rationale = result.rationale();
        this.decidedAt = result.decidedAt();
        this.signals = new ArrayList<>(result.signals());
    }

    public static DecisionEntity from(DecisionResult result) {
        return new DecisionEntity(result);
    }

    public DecisionResult toResult() {
        return new DecisionResult(decisionId, transactionId, outcome, rationale, signals, decidedAt);
    }
}
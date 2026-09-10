package com.cosmess.finguard.dispute.agent.application;

import com.cosmess.finguard.dispute.agent.tools.DisputeReadOnlyTools;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DisputeAgentOrchestrator {

    private final DisputeReadOnlyTools tools;

    public DisputeAgentOrchestrator(DisputeReadOnlyTools tools) {
        this.tools = tools;
    }

    public DisputeAgentRecommendation investigate(UUID disputeId) {
        DisputeAgentContext context = tools.context(disputeId);
        String status = context.status();
        if ("UNKNOWN".equals(status)) {
            return new DisputeAgentRecommendation(
                    disputeId,
                    "REQUEST_CONTEXT",
                    "Dispute context is unavailable; request read-only context before review.",
                    List.of("DISPUTE_STATUS_UNKNOWN")
            );
        }
        if (context.evidenceReferences().isEmpty()) {
            return new DisputeAgentRecommendation(
                    disputeId,
                    "REQUEST_EVIDENCE",
                    "No evidence is attached; request evidence before human review.",
                    List.of("NO_EVIDENCE")
            );
        }
        return new DisputeAgentRecommendation(
                disputeId,
                "REVIEW_MANUALLY",
                "Evidence is available; a human reviewer must make the dispute decision.",
                List.of("EVIDENCE_AVAILABLE", "HUMAN_REVIEW_REQUIRED")
        );
    }
}
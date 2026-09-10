package com.cosmess.finguard.dispute.agent.application;

import com.cosmess.finguard.dispute.agent.tools.DisputeReadOnlyTools;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DisputeAgentOrchestratorTest {

    @Test
    void requestsEvidenceWhenDisputeHasNoEvidence() {
        UUID disputeId = UUID.randomUUID();
        DisputeAgentOrchestrator orchestrator = new DisputeAgentOrchestrator(new DisputeReadOnlyTools(Map.of(
                disputeId, new DisputeAgentContext(disputeId, "EVIDENCE_REQUESTED", "Unauthorized charge", List.of())
        )));

        DisputeAgentRecommendation recommendation = orchestrator.investigate(disputeId);

        assertThat(recommendation.action()).isEqualTo("REQUEST_EVIDENCE");
        assertThat(recommendation.evidence()).containsExactly("NO_EVIDENCE");
    }

    @Test
    void requiresHumanReviewWhenEvidenceExists() {
        UUID disputeId = UUID.randomUUID();
        DisputeAgentOrchestrator orchestrator = new DisputeAgentOrchestrator(new DisputeReadOnlyTools(Map.of(
                disputeId, new DisputeAgentContext(disputeId, "UNDER_REVIEW", "Unauthorized charge", List.of("s3://evidence/1"))
        )));

        assertThat(orchestrator.investigate(disputeId).action()).isEqualTo("REVIEW_MANUALLY");
    }
}
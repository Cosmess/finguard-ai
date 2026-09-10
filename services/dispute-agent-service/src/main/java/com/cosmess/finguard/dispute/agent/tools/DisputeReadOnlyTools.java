package com.cosmess.finguard.dispute.agent.tools;

import com.cosmess.finguard.dispute.agent.application.DisputeAgentContext;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class DisputeReadOnlyTools {

    private final Map<UUID, DisputeAgentContext> contexts;

    public DisputeReadOnlyTools() {
        this(Map.of());
    }

    public DisputeReadOnlyTools(Map<UUID, DisputeAgentContext> contexts) {
        this.contexts = Map.copyOf(contexts);
    }

    @Tool("Reads the current dispute status and reason without changing the dispute")
    public String readDispute(UUID disputeId) {
        return context(disputeId).status() + ": " + context(disputeId).reason();
    }

    @Tool("Reads the references of evidence already attached to a dispute without changing it")
    public String readEvidence(UUID disputeId) {
        return String.join(",", context(disputeId).evidenceReferences());
    }

    public DisputeAgentContext context(UUID disputeId) {
        return contexts.getOrDefault(disputeId, new DisputeAgentContext(
                disputeId,
                "UNKNOWN",
                "No dispute context available",
                java.util.List.of()
        ));
    }
}
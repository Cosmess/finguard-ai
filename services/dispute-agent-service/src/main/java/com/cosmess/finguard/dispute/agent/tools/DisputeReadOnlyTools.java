package com.cosmess.finguard.dispute.agent.tools;

import com.cosmess.finguard.dispute.agent.application.DisputeAgentContext;
import com.cosmess.finguard.dispute.agent.application.DisputeContextReader;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class DisputeReadOnlyTools {

    private final DisputeContextReader contextReader;

    @Autowired
    public DisputeReadOnlyTools(DisputeContextReader contextReader) {
        this.contextReader = contextReader;
    }

    public DisputeReadOnlyTools(Map<UUID, DisputeAgentContext> contexts) {
        Map<UUID, DisputeAgentContext> immutableContexts = Map.copyOf(contexts);
        this.contextReader = immutableContexts::get;
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
        DisputeAgentContext context = contextReader.read(disputeId);
        if (context != null) {
            return context;
        }
        return new DisputeAgentContext(
                disputeId,
                "UNKNOWN",
                "No dispute context available",
                java.util.List.of()
        );
    }
}
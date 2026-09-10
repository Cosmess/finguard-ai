package com.cosmess.finguard.dispute.agent.application;

import java.util.UUID;

@FunctionalInterface
public interface DisputeContextReader {

    DisputeAgentContext read(UUID disputeId);
}
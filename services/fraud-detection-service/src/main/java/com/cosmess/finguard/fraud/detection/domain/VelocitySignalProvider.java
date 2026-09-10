package com.cosmess.finguard.fraud.detection.domain;

import java.util.Optional;

public interface VelocitySignalProvider {

    Optional<VelocitySignal> customerVelocity(String customerId);
}

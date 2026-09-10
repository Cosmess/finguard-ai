package com.cosmess.finguard.fraud.detection.application;

import com.cosmess.finguard.fraud.detection.domain.VelocitySignal;
import com.cosmess.finguard.fraud.detection.domain.VelocitySignalProvider;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class DatabaseVelocitySignalProvider implements VelocitySignalProvider {

    private static final String CUSTOMER = "CUSTOMER";

    private final VelocityRepository velocityRepository;

    DatabaseVelocitySignalProvider(VelocityRepository velocityRepository) {
        this.velocityRepository = velocityRepository;
    }

    @Override
    public Optional<VelocitySignal> customerVelocity(String customerId) {
        return velocityRepository.findByDimensionAndValue(CUSTOMER, customerId)
                .map(snapshot -> new VelocitySignal(snapshot.transactionCount()));
    }
}

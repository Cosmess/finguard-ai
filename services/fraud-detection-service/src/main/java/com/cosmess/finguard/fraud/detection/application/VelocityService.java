package com.cosmess.finguard.fraud.detection.application;

import com.cosmess.finguard.events.fraud.FraudVelocityUpdatedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VelocityService {

    private final VelocityRepository velocityRepository;
    private final ClockProvider clockProvider;

    public VelocityService(VelocityRepository velocityRepository, ClockProvider clockProvider) {
        this.velocityRepository = velocityRepository;
        this.clockProvider = clockProvider;
    }

    @Transactional
    public VelocitySnapshot update(FraudVelocityUpdatedEvent event) {
        VelocitySnapshot snapshot = new VelocitySnapshot(
                event.dimension(),
                event.dimensionValue(),
                event.transactionCount(),
                event.windowStart(),
                event.windowEnd(),
                clockProvider.now()
        );

        return velocityRepository.save(snapshot);
    }
}

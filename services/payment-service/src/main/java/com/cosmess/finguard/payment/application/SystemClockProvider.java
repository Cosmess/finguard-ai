package com.cosmess.finguard.payment.application;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
class SystemClockProvider implements ClockProvider {

    @Override
    public Instant now() {
        return Instant.now();
    }
}

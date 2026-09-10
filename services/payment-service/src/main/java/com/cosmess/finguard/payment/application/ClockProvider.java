package com.cosmess.finguard.payment.application;

import java.time.Instant;

public interface ClockProvider {

    Instant now();
}

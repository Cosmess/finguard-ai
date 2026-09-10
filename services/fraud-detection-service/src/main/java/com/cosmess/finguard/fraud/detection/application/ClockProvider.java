package com.cosmess.finguard.fraud.detection.application;

import java.time.Instant;

public interface ClockProvider {

    Instant now();
}

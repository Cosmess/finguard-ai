package com.cosmess.finguard.fraud.detection.application;

public interface FraudEventPublisher {

    void publishFraudSuspected(FraudCase fraudCase);
}

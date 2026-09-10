package com.cosmess.finguard.fraud.detection.domain;

import com.cosmess.finguard.events.payment.TransactionCreatedEvent;

import java.util.Optional;

class NewDeviceRule implements FraudRule {

    @Override
    public Optional<FraudRuleResult> evaluate(TransactionCreatedEvent transaction) {
        if (transaction.deviceId() == null || transaction.deviceId().isBlank() || transaction.deviceId().startsWith("new-")) {
            return Optional.of(new FraudRuleResult("NEW_DEVICE", 25));
        }
        return Optional.empty();
    }
}

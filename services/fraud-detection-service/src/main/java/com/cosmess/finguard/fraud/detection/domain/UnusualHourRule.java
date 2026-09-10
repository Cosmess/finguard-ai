package com.cosmess.finguard.fraud.detection.domain;

import com.cosmess.finguard.events.payment.TransactionCreatedEvent;

import java.time.ZoneOffset;
import java.util.Optional;

class UnusualHourRule implements FraudRule {

    @Override
    public Optional<FraudRuleResult> evaluate(TransactionCreatedEvent transaction) {
        int hour = transaction.createdAt().atZone(ZoneOffset.UTC).getHour();
        if (hour < 5) {
            return Optional.of(new FraudRuleResult("UNUSUAL_HOUR", 15));
        }
        return Optional.empty();
    }
}

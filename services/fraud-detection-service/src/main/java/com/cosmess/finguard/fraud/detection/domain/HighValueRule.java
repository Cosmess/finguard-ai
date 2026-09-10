package com.cosmess.finguard.fraud.detection.domain;

import com.cosmess.finguard.events.payment.TransactionCreatedEvent;

import java.math.BigDecimal;
import java.util.Optional;

class HighValueRule implements FraudRule {

    private static final BigDecimal THRESHOLD = new BigDecimal("5000.00");

    @Override
    public Optional<FraudRuleResult> evaluate(TransactionCreatedEvent transaction) {
        if (transaction.amount().compareTo(THRESHOLD) >= 0) {
            return Optional.of(new FraudRuleResult("HIGH_VALUE", 50));
        }
        return Optional.empty();
    }
}

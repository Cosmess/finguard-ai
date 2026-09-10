package com.cosmess.finguard.fraud.detection.domain;

import com.cosmess.finguard.events.payment.TransactionCreatedEvent;

import java.util.Optional;

interface FraudRule {

    Optional<FraudRuleResult> evaluate(TransactionCreatedEvent transaction);
}

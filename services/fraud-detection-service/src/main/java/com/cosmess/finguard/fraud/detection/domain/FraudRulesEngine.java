package com.cosmess.finguard.fraud.detection.domain;

import com.cosmess.finguard.events.payment.TransactionCreatedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FraudRulesEngine {

    private final List<FraudRule> rules = List.of(
            new HighValueRule(),
            new NewDeviceRule(),
            new UnusualHourRule()
    );

    public FraudAssessment assess(TransactionCreatedEvent transaction) {
        List<FraudRuleResult> results = rules.stream()
                .map(rule -> rule.evaluate(transaction))
                .flatMap(Optional::stream)
                .toList();

        int score = results.stream().mapToInt(FraudRuleResult::score).sum();
        List<String> triggeredRules = results.stream().map(FraudRuleResult::ruleName).toList();

        return new FraudAssessment(score, riskLevelFor(score), triggeredRules);
    }

    private RiskLevel riskLevelFor(int score) {
        if (score >= 50) {
            return RiskLevel.HIGH;
        }
        if (score >= 25) {
            return RiskLevel.MEDIUM;
        }
        return RiskLevel.LOW;
    }
}

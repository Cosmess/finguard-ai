package com.cosmess.finguard.fraud.detection.domain;

import java.util.List;

public record FraudAssessment(
        int score,
        RiskLevel riskLevel,
        List<String> triggeredRules
) {
}

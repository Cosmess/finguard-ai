package com.cosmess.finguard.fraud.detection.application;

import com.cosmess.finguard.events.payment.TransactionCreatedEvent;
import com.cosmess.finguard.fraud.detection.domain.FraudAssessment;
import com.cosmess.finguard.fraud.detection.domain.FraudRulesEngine;
import com.cosmess.finguard.fraud.detection.domain.RiskLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FraudDetectionService {

    private final FraudRulesEngine fraudRulesEngine;
    private final FraudCaseRepository fraudCaseRepository;
    private final ProcessedTransactionRepository processedTransactionRepository;
    private final FraudEventPublisher fraudEventPublisher;
    private final ClockProvider clockProvider;

    public FraudDetectionService(
            FraudRulesEngine fraudRulesEngine,
            FraudCaseRepository fraudCaseRepository,
            ProcessedTransactionRepository processedTransactionRepository,
            FraudEventPublisher fraudEventPublisher,
            ClockProvider clockProvider
    ) {
        this.fraudRulesEngine = fraudRulesEngine;
        this.fraudCaseRepository = fraudCaseRepository;
        this.processedTransactionRepository = processedTransactionRepository;
        this.fraudEventPublisher = fraudEventPublisher;
        this.clockProvider = clockProvider;
    }

    @Transactional
    public void evaluate(TransactionCreatedEvent transaction) {
        if (processedTransactionRepository.exists(transaction.transactionId())) {
            return;
        }

        FraudAssessment assessment = fraudRulesEngine.assess(transaction);
        processedTransactionRepository.markProcessed(transaction.transactionId());

        if (assessment.riskLevel() == RiskLevel.LOW) {
            return;
        }

        FraudCase fraudCase = new FraudCase(
                UUID.randomUUID(),
                transaction.transactionId(),
                transaction.customerId(),
                transaction.amount(),
                transaction.currency(),
                assessment.score(),
                assessment.riskLevel(),
                assessment.triggeredRules(),
                clockProvider.now()
        );

        FraudCase saved = fraudCaseRepository.save(fraudCase);
        fraudEventPublisher.publishFraudSuspected(saved);
    }
}

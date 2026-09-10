package com.cosmess.finguard.payment.application;

import com.cosmess.finguard.payment.domain.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final TransactionEventRecorder transactionEventRecorder;
    private final ClockProvider clockProvider;

    public PaymentService(
            TransactionRepository transactionRepository,
            TransactionEventRecorder transactionEventRecorder,
            ClockProvider clockProvider
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionEventRecorder = transactionEventRecorder;
        this.clockProvider = clockProvider;
    }

    @Transactional
    public Transaction create(TransactionCommand command) {
        Transaction transaction = Transaction.create(
                command.merchantId(),
                command.customerId(),
                command.amount(),
                command.currency(),
                command.paymentMethod(),
                command.deviceId(),
                command.ipAddress(),
                clockProvider.now()
        );

        Transaction saved = transactionRepository.save(transaction);
        transactionEventRecorder.recordTransactionCreated(saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public Transaction getById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }
}

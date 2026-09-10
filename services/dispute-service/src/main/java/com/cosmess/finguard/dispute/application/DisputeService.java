package com.cosmess.finguard.dispute.application;

import com.cosmess.finguard.dispute.adapter.out.persistence.DisputeEntity;
import com.cosmess.finguard.dispute.adapter.out.persistence.DisputeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
public class DisputeService {

    private final DisputeRepository repository;
    private final Clock clock;

    @Autowired
    public DisputeService(DisputeRepository repository) {
        this(repository, Clock.systemUTC());
    }

    DisputeService(DisputeRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public Dispute open(UUID transactionId, String customerId, String reason) {
        var now = clock.instant();
        var dispute = new Dispute(UUID.randomUUID(), transactionId, customerId, reason, DisputeStatus.OPEN, null, null, now, now, java.util.List.of());
        return save(dispute);
    }

    @Transactional
    public Dispute requestEvidence(UUID id) {
        return transition(id, DisputeStatus.EVIDENCE_REQUESTED, null, null);
    }

    @Transactional
    public Dispute addEvidence(UUID id, String type, String reference, String description) {
        Dispute dispute = find(id);
        if (dispute.status() != DisputeStatus.OPEN && dispute.status() != DisputeStatus.EVIDENCE_REQUESTED) {
            throw new IllegalStateException("Evidence cannot be added in status " + dispute.status());
        }
        var evidence = new Dispute.Evidence(UUID.randomUUID(), type, reference, description, clock.instant());
        var updated = new Dispute(dispute.id(), dispute.transactionId(), dispute.customerId(), dispute.reason(), DisputeStatus.UNDER_REVIEW, dispute.reviewerId(), dispute.reviewDecision(), dispute.createdAt(), clock.instant(), append(dispute, evidence));
        return save(updated);
    }

    @Transactional
    public Dispute review(UUID id, String reviewerId, ReviewDecision decision) {
        if (reviewerId == null || reviewerId.isBlank()) {
            throw new IllegalArgumentException("reviewerId is required");
        }
        Dispute dispute = find(id);
        if (dispute.status() != DisputeStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Dispute must be under review");
        }
        return save(new Dispute(dispute.id(), dispute.transactionId(), dispute.customerId(), dispute.reason(), decision == ReviewDecision.APPROVE ? DisputeStatus.RESOLVED : DisputeStatus.REJECTED, reviewerId, decision, dispute.createdAt(), clock.instant(), dispute.evidence()));
    }

    @Transactional(readOnly = true)
    public Dispute get(UUID id) {
        return find(id);
    }

    private Dispute transition(UUID id, DisputeStatus target, String reviewerId, ReviewDecision decision) {
        Dispute dispute = find(id);
        if (target == DisputeStatus.EVIDENCE_REQUESTED && dispute.status() != DisputeStatus.OPEN) {
            throw new IllegalStateException("Only open disputes can request evidence");
        }
        return save(new Dispute(dispute.id(), dispute.transactionId(), dispute.customerId(), dispute.reason(), target, reviewerId, decision, dispute.createdAt(), clock.instant(), dispute.evidence()));
    }

    private Dispute find(UUID id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Dispute not found: " + id)).toDispute();
    }

    private Dispute save(Dispute dispute) {
        return repository.save(DisputeEntity.from(dispute)).toDispute();
    }

    private java.util.List<Dispute.Evidence> append(Dispute dispute, Dispute.Evidence evidence) {
        var values = new java.util.ArrayList<>(dispute.evidence());
        values.add(evidence);
        return values;
    }
}
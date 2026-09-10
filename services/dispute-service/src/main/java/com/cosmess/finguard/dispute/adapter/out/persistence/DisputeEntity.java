package com.cosmess.finguard.dispute.adapter.out.persistence;

import com.cosmess.finguard.dispute.application.Dispute;
import com.cosmess.finguard.dispute.application.DisputeStatus;
import com.cosmess.finguard.dispute.application.ReviewDecision;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "disputes")
public class DisputeEntity {

    @Id
    private UUID id;
    private UUID transactionId;
    private String customerId;
    private String reason;
    @Enumerated(EnumType.STRING)
    private DisputeStatus status;
    private String reviewerId;
    @Enumerated(EnumType.STRING)
    private ReviewDecision reviewDecision;
    private Instant createdAt;
    private Instant updatedAt;

    @OneToMany(mappedBy = "dispute", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvidenceEntity> evidence = new ArrayList<>();

    protected DisputeEntity() {
    }

    private DisputeEntity(Dispute dispute) {
        this.id = dispute.id();
        this.transactionId = dispute.transactionId();
        this.customerId = dispute.customerId();
        this.reason = dispute.reason();
        this.status = dispute.status();
        this.reviewerId = dispute.reviewerId();
        this.reviewDecision = dispute.reviewDecision();
        this.createdAt = dispute.createdAt();
        this.updatedAt = dispute.updatedAt();
        this.evidence = dispute.evidence().stream()
                .map(item -> EvidenceEntity.from(this, item))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    public static DisputeEntity from(Dispute dispute) {
        return new DisputeEntity(dispute);
    }

    public Dispute toDispute() {
        return new Dispute(
                id,
                transactionId,
                customerId,
                reason,
                status,
                reviewerId,
                reviewDecision,
                createdAt,
                updatedAt,
                evidence.stream().map(evidenceItem -> evidenceItem.toEvidence()).toList()
        );
    }
}
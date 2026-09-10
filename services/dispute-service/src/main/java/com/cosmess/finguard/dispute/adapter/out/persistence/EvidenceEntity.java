package com.cosmess.finguard.dispute.adapter.out.persistence;

import com.cosmess.finguard.dispute.application.Dispute;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dispute_evidence")
public class EvidenceEntity {

    @Id
    private UUID id;
    private String type;
    private String reference;
    private String description;
    private Instant addedAt;

    @ManyToOne
    @JoinColumn(name = "dispute_id", nullable = false)
    private DisputeEntity dispute;

    protected EvidenceEntity() {
    }

    private EvidenceEntity(DisputeEntity dispute, Dispute.Evidence evidence) {
        this.id = evidence.id();
        this.dispute = dispute;
        this.type = evidence.type();
        this.reference = evidence.reference();
        this.description = evidence.description();
        this.addedAt = evidence.addedAt();
    }

    static EvidenceEntity from(DisputeEntity dispute, Dispute.Evidence evidence) {
        return new EvidenceEntity(dispute, evidence);
    }

    Dispute.Evidence toEvidence() {
        return new Dispute.Evidence(id, type, reference, description, addedAt);
    }
}
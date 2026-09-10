package com.cosmess.finguard.fraud.detection.adapter.out.persistence;

import com.cosmess.finguard.fraud.detection.application.VelocitySnapshot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@IdClass(VelocitySnapshotId.class)
@Table(name = "velocity_snapshots")
class VelocitySnapshotEntity {

    @Id
    @Column(nullable = false, length = 40)
    private String dimension;

    @Id
    @Column(nullable = false, length = 120)
    private String dimensionValue;

    @Column(nullable = false)
    private long transactionCount;

    @Column(nullable = false)
    private Instant windowStart;

    @Column(nullable = false)
    private Instant windowEnd;

    @Column(nullable = false)
    private Instant updatedAt;

    protected VelocitySnapshotEntity() {
    }

    private VelocitySnapshotEntity(VelocitySnapshot snapshot) {
        this.dimension = snapshot.dimension();
        this.dimensionValue = snapshot.dimensionValue();
        this.transactionCount = snapshot.transactionCount();
        this.windowStart = snapshot.windowStart();
        this.windowEnd = snapshot.windowEnd();
        this.updatedAt = snapshot.updatedAt();
    }

    static VelocitySnapshotEntity fromDomain(VelocitySnapshot snapshot) {
        return new VelocitySnapshotEntity(snapshot);
    }

    VelocitySnapshot toDomain() {
        return new VelocitySnapshot(dimension, dimensionValue, transactionCount, windowStart, windowEnd, updatedAt);
    }
}

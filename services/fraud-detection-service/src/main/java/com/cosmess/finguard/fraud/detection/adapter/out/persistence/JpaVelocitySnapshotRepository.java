package com.cosmess.finguard.fraud.detection.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface JpaVelocitySnapshotRepository extends JpaRepository<VelocitySnapshotEntity, VelocitySnapshotId> {
}

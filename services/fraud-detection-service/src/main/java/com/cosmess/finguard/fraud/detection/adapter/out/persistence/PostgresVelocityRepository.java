package com.cosmess.finguard.fraud.detection.adapter.out.persistence;

import com.cosmess.finguard.fraud.detection.application.VelocityRepository;
import com.cosmess.finguard.fraud.detection.application.VelocitySnapshot;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class PostgresVelocityRepository implements VelocityRepository {

    private final JpaVelocitySnapshotRepository repository;

    PostgresVelocityRepository(JpaVelocitySnapshotRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<VelocitySnapshot> findByDimensionAndValue(String dimension, String value) {
        return repository.findById(new VelocitySnapshotId(dimension, value)).map(VelocitySnapshotEntity::toDomain);
    }

    @Override
    public VelocitySnapshot save(VelocitySnapshot snapshot) {
        return repository.save(VelocitySnapshotEntity.fromDomain(snapshot)).toDomain();
    }
}

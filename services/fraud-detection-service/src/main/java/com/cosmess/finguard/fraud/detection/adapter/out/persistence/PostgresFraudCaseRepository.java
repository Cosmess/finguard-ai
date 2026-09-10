package com.cosmess.finguard.fraud.detection.adapter.out.persistence;

import com.cosmess.finguard.fraud.detection.application.FraudCase;
import com.cosmess.finguard.fraud.detection.application.FraudCaseRepository;
import org.springframework.stereotype.Repository;

@Repository
class PostgresFraudCaseRepository implements FraudCaseRepository {

    private final JpaFraudCaseRepository repository;

    PostgresFraudCaseRepository(JpaFraudCaseRepository repository) {
        this.repository = repository;
    }

    @Override
    public FraudCase save(FraudCase fraudCase) {
        return repository.save(FraudCaseEntity.fromDomain(fraudCase)).toDomain();
    }
}

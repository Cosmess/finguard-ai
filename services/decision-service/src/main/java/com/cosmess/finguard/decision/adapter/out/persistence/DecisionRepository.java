package com.cosmess.finguard.decision.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DecisionRepository extends JpaRepository<DecisionEntity, UUID> {
}
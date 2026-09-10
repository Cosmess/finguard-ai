package com.cosmess.finguard.fraud.detection.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface JpaProcessedTransactionRepository extends JpaRepository<ProcessedTransactionEntity, UUID> {
}

package com.cosmess.finguard.payment.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface JpaTransactionRepository extends JpaRepository<TransactionEntity, UUID> {
}

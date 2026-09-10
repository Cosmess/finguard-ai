package com.cosmess.finguard.dispute.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DisputeRepository extends JpaRepository<DisputeEntity, UUID> {
}
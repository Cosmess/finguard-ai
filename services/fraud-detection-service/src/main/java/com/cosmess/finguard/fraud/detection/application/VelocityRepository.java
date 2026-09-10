package com.cosmess.finguard.fraud.detection.application;

import java.util.Optional;

public interface VelocityRepository {

    Optional<VelocitySnapshot> findByDimensionAndValue(String dimension, String value);

    VelocitySnapshot save(VelocitySnapshot snapshot);
}

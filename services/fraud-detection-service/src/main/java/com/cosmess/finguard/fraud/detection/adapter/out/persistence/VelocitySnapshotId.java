package com.cosmess.finguard.fraud.detection.adapter.out.persistence;

import java.io.Serializable;

class VelocitySnapshotId implements Serializable {

    private String dimension;
    private String dimensionValue;

    protected VelocitySnapshotId() {
    }

    VelocitySnapshotId(String dimension, String dimensionValue) {
        this.dimension = dimension;
        this.dimensionValue = dimensionValue;
    }
}

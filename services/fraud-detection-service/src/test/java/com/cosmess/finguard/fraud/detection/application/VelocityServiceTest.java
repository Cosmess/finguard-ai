package com.cosmess.finguard.fraud.detection.application;

import com.cosmess.finguard.events.fraud.FraudVelocityUpdatedEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class VelocityServiceTest {

    @Test
    void storesLatestVelocitySnapshot() {
        InMemoryVelocityRepository repository = new InMemoryVelocityRepository();
        VelocityService service = new VelocityService(repository, () -> Instant.parse("2026-09-10T12:10:00Z"));

        VelocitySnapshot snapshot = service.update(new FraudVelocityUpdatedEvent(
                "CUSTOMER",
                "customer-1",
                6,
                Instant.parse("2026-09-10T12:00:00Z"),
                Instant.parse("2026-09-10T12:10:00Z")
        ));

        assertThat(snapshot.transactionCount()).isEqualTo(6);
        assertThat(repository.findByDimensionAndValue("CUSTOMER", "customer-1")).contains(snapshot);
    }

    private static class InMemoryVelocityRepository implements VelocityRepository {

        private final Map<String, VelocitySnapshot> snapshots = new HashMap<>();

        @Override
        public Optional<VelocitySnapshot> findByDimensionAndValue(String dimension, String value) {
            return Optional.ofNullable(snapshots.get(key(dimension, value)));
        }

        @Override
        public VelocitySnapshot save(VelocitySnapshot snapshot) {
            snapshots.put(key(snapshot.dimension(), snapshot.dimensionValue()), snapshot);
            return snapshot;
        }

        private String key(String dimension, String value) {
            return dimension + ":" + value;
        }
    }
}

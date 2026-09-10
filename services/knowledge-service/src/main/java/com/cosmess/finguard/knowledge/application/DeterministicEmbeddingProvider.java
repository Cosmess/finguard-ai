package com.cosmess.finguard.knowledge.application;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class DeterministicEmbeddingProvider {

    public String embed(String text) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(text.toLowerCase().getBytes(StandardCharsets.UTF_8));
            int first = digest[0] & 0xff;
            int second = digest[1] & 0xff;
            int third = digest[2] & 0xff;
            double magnitude = Math.sqrt(first * first + second * second + third * third);
            return "%s,%s,%s".formatted(first / magnitude, second / magnitude, third / magnitude);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not generate deterministic embedding", exception);
        }
    }

    public double similarity(String left, String right) {
        double[] leftVector = parse(left);
        double[] rightVector = parse(right);
        return leftVector[0] * rightVector[0]
                + leftVector[1] * rightVector[1]
                + leftVector[2] * rightVector[2];
    }

    private double[] parse(String embedding) {
        String[] values = embedding.split(",");
        if (values.length != 3) {
            throw new IllegalArgumentException("Embedding must contain three dimensions");
        }
        return new double[]{
                Double.parseDouble(values[0]),
                Double.parseDouble(values[1]),
                Double.parseDouble(values[2])
        };
    }
}
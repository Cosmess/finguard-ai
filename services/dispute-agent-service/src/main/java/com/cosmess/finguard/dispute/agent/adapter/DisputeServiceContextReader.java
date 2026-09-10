package com.cosmess.finguard.dispute.agent.adapter;

import com.cosmess.finguard.dispute.agent.application.DisputeAgentContext;
import com.cosmess.finguard.dispute.agent.application.DisputeContextReader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DisputeServiceContextReader implements DisputeContextReader {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public DisputeServiceContextReader(
            @Value("${dispute.agent.dispute-service-url:http://localhost:8084}") String disputeServiceUrl
    ) {
        this.restClient = RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory(
                        HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build()
                ))
                .baseUrl(disputeServiceUrl)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public DisputeAgentContext read(UUID disputeId) {
        try {
            String body = restClient.get()
                    .uri("/disputes/{id}", disputeId)
                    .retrieve()
                    .body(String.class);
            JsonNode dispute = objectMapper.readTree(body);
            List<String> references = new ArrayList<>();
            for (JsonNode evidence : dispute.path("evidence")) {
                references.add(evidence.path("reference").asText());
            }
            return new DisputeAgentContext(
                    disputeId,
                    dispute.path("status").asText("UNKNOWN"),
                    dispute.path("reason").asText("No dispute context available"),
                    references.stream().filter(reference -> !reference.isBlank()).toList()
            );
        } catch (Exception exception) {
            return unknown(disputeId);
        }
    }

    private DisputeAgentContext unknown(UUID disputeId) {
        return new DisputeAgentContext(
                disputeId,
                "UNKNOWN",
                "Dispute context unavailable",
                List.of()
        );
    }
}
package com.cosmess.finguard.decision.adapter.in.web;

import com.cosmess.finguard.decision.application.DecisionPolicyService;
import com.cosmess.finguard.decision.application.DecisionRequest;
import com.cosmess.finguard.decision.application.DecisionResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/decisions")
public class DecisionController {

    private final DecisionPolicyService service;

    public DecisionController(DecisionPolicyService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DecisionResult decide(@Valid @RequestBody DecisionRequestPayload payload) {
        return service.decide(new DecisionRequest(payload.transactionId(), payload.fraudScore(), payload.fraudRiskLevel(), payload.aiRecommendation(), payload.disputeStatus()));
    }

    public record DecisionRequestPayload(
            @NotNull UUID transactionId,
            @Min(0) @Max(100) int fraudScore,
            @NotBlank String fraudRiskLevel,
            @NotBlank String aiRecommendation,
            String disputeStatus
    ) {
    }
}
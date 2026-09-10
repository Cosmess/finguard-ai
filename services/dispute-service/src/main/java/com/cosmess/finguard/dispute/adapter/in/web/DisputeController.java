package com.cosmess.finguard.dispute.adapter.in.web;

import com.cosmess.finguard.dispute.application.Dispute;
import com.cosmess.finguard.dispute.application.DisputeService;
import com.cosmess.finguard.dispute.application.ReviewDecision;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/disputes")
public class DisputeController {

    private final DisputeService service;

    public DisputeController(DisputeService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Dispute open(@Valid @RequestBody OpenDisputeRequest request) {
        return service.open(request.transactionId(), request.customerId(), request.reason());
    }

    @GetMapping("/{id}")
    public Dispute get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping("/{id}/evidence-request")
    public Dispute requestEvidence(@PathVariable UUID id) {
        return service.requestEvidence(id);
    }

    @PostMapping("/{id}/evidence")
    public Dispute addEvidence(@PathVariable UUID id, @Valid @RequestBody EvidenceRequest request) {
        return service.addEvidence(id, request.type(), request.reference(), request.description());
    }

    @PostMapping("/{id}/review")
    public Dispute review(@PathVariable UUID id, @Valid @RequestBody ReviewRequest request) {
        return service.review(id, request.reviewerId(), request.decision());
    }

    public record OpenDisputeRequest(@NotNull UUID transactionId, @NotBlank String customerId, @NotBlank @Size(max = 500) String reason) {
    }

    public record EvidenceRequest(@NotBlank String type, @NotBlank String reference, @NotBlank @Size(max = 1000) String description) {
    }

    public record ReviewRequest(@NotBlank String reviewerId, @NotNull ReviewDecision decision) {
    }
}
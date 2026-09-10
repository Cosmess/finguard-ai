package com.cosmess.finguard.knowledge.adapter.in.web;

import com.cosmess.finguard.knowledge.application.KnowledgeDocument;
import com.cosmess.finguard.knowledge.application.KnowledgeDocumentService;
import com.cosmess.finguard.knowledge.application.KnowledgeSearchResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/knowledge")
public class KnowledgeDocumentController {

    private final KnowledgeDocumentService service;

    public KnowledgeDocumentController(KnowledgeDocumentService service) {
        this.service = service;
    }

    @PostMapping("/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public KnowledgeDocument ingest(@Valid @RequestBody IngestDocumentRequest request) {
        return service.ingest(request.title(), request.sourceUri(), request.content());
    }

    @GetMapping("/search")
    public List<KnowledgeSearchResult> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return service.search(query, limit);
    }

    public record IngestDocumentRequest(
            @NotBlank @Size(max = 200) String title,
            @NotBlank @Size(max = 500) String sourceUri,
            @NotBlank @Size(max = 100_000) String content
    ) {
    }
}
package org.nikolait.crmsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nikolait.crmsystem.dto.TransactionCreateRequest;
import org.nikolait.crmsystem.dto.TransactionResponse;
import org.nikolait.crmsystem.service.TransactionService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public Page<TransactionResponse> getAll(
            @RequestParam(required = false) Long sellerId,
            @ParameterObject Pageable pageable
    ) {
        if (sellerId != null) {
            return transactionService.getBySeller(sellerId, pageable);
        }
        return transactionService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public TransactionResponse getById(@PathVariable Long id) {
        return transactionService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@Valid @RequestBody TransactionCreateRequest request) {
        return transactionService.createPending(request);
    }

    @PostMapping("/{id}/complete")
    public TransactionResponse complete(@PathVariable Long id) {
        return transactionService.complete(id);
    }
}

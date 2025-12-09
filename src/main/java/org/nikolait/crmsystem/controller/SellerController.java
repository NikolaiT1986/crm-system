package org.nikolait.crmsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nikolait.crmsystem.dto.SellerCreateRequest;
import org.nikolait.crmsystem.dto.SellerResponse;
import org.nikolait.crmsystem.dto.SellerUpdateRequest;
import org.nikolait.crmsystem.service.SellerService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @GetMapping
    public Page<SellerResponse> getAll(@ParameterObject Pageable pageable) {
        return sellerService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public SellerResponse getById(@PathVariable Long id) {
        return sellerService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SellerResponse create(@Valid @RequestBody SellerCreateRequest request) {
        return sellerService.create(request);
    }

    @PatchMapping("/{id}")
    public SellerResponse update(@PathVariable Long id,
                                 @Valid @RequestBody SellerUpdateRequest request) {
        return sellerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        sellerService.delete(id);
    }
}

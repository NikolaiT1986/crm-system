package org.nikolait.crmsystem.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.nikolait.crmsystem.dto.SellerCreateRequest;
import org.nikolait.crmsystem.dto.SellerResponse;
import org.nikolait.crmsystem.dto.SellerUpdateRequest;
import org.nikolait.crmsystem.mapper.SellerMapper;
import org.nikolait.crmsystem.model.Seller;
import org.nikolait.crmsystem.repository.SellerRepository;
import org.nikolait.crmsystem.service.SellerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepository;
    private final SellerMapper sellerMapper;

    @Override
    public Page<SellerResponse> getAll(Pageable pageable) {
        return sellerRepository.findAllByDeletedFalse(pageable)
                .map(sellerMapper::toResponse);
    }

    @Override
    public SellerResponse getById(Long id) {
        Seller seller = sellerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found: " + id));

        return sellerMapper.toResponse(seller);
    }

    @Override
    public SellerResponse create(SellerCreateRequest request) {
        Seller seller = sellerMapper.toEntity(request);
        return sellerMapper.toResponse(sellerRepository.save(seller));
    }

    @Override
    public SellerResponse update(Long id, SellerUpdateRequest request) {
        Seller seller = sellerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found: " + id));

        sellerMapper.patchFromRequest(request, seller);
        Seller saved = sellerRepository.save(seller);
        return sellerMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        Seller seller = sellerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found: " + id));

        seller.setDeleted(true);
        sellerRepository.save(seller);
    }
}

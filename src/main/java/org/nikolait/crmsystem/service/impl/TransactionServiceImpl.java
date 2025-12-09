package org.nikolait.crmsystem.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.nikolait.crmsystem.dto.TransactionCreateRequest;
import org.nikolait.crmsystem.dto.TransactionResponse;
import org.nikolait.crmsystem.exception.InvalidSellerException;
import org.nikolait.crmsystem.exception.InvalidTransactionException;
import org.nikolait.crmsystem.mapper.TransactionMapper;
import org.nikolait.crmsystem.model.Seller;
import org.nikolait.crmsystem.model.Transaction;
import org.nikolait.crmsystem.model.enums.TransactionStatus;
import org.nikolait.crmsystem.repository.SellerRepository;
import org.nikolait.crmsystem.repository.TransactionRepository;
import org.nikolait.crmsystem.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public Page<TransactionResponse> getAll(Pageable pageable) {
        return transactionRepository.findAll(pageable)
                .map(transactionMapper::toResponse);
    }

    @Override
    public Page<TransactionResponse> getBySeller(Long sellerId, Pageable pageable) {
        return transactionRepository.findAllBySellerId(sellerId, pageable)
                .map(transactionMapper::toResponse);
    }

    @Override
    public TransactionResponse getById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found: " + id));

        return transactionMapper.toResponse(transaction);
    }

    @Override
    public TransactionResponse createPending(TransactionCreateRequest request) {
        Seller seller = sellerRepository.findByIdAndDeletedFalse(request.sellerId())
                .orElseThrow(() -> new InvalidSellerException("Seller not found: " + request.sellerId()));

        Transaction transaction = transactionMapper.ToEntity(request);
        transaction.setSeller(seller);
        transaction.setStatus(TransactionStatus.PENDING);

        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    public TransactionResponse complete(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found: " + id));

        if (transaction.getStatus() == TransactionStatus.COMPLETED) {
            return transactionMapper.toResponse(transaction);
        }

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new InvalidTransactionException(
                    "Transaction cannot be completed from status: " + transaction.getStatus()
            );
        }

        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setTransactionDate(LocalDateTime.now());

        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }
}

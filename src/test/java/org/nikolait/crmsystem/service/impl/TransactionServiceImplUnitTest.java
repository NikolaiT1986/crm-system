package org.nikolait.crmsystem.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nikolait.crmsystem.dto.TransactionCreateRequest;
import org.nikolait.crmsystem.dto.TransactionResponse;
import org.nikolait.crmsystem.exception.InvalidSellerException;
import org.nikolait.crmsystem.exception.InvalidTransactionException;
import org.nikolait.crmsystem.mapper.TransactionMapper;
import org.nikolait.crmsystem.model.Seller;
import org.nikolait.crmsystem.model.Transaction;
import org.nikolait.crmsystem.model.enums.PaymentType;
import org.nikolait.crmsystem.model.enums.TransactionStatus;
import org.nikolait.crmsystem.repository.SellerRepository;
import org.nikolait.crmsystem.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplUnitTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    // --------------------------------------------
    //                getAll()
    // --------------------------------------------
    @Test
    void getAll_returnsPageOfResponses() {
        Transaction tx = new Transaction();
        TransactionResponse response = new TransactionResponse(
                1L, 10L, new BigDecimal("100.00"),
                PaymentType.CASH, LocalDateTime.now(),
                TransactionStatus.PENDING, LocalDateTime.now()
        );

        Pageable pageable = Pageable.ofSize(10);

        when(transactionRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(tx)));
        when(transactionMapper.toResponse(tx)).thenReturn(response);

        Page<TransactionResponse> result = transactionService.getAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().getFirst().id());
        verify(transactionRepository).findAll(pageable);
        verify(transactionMapper).toResponse(tx);
    }

    // --------------------------------------------
    //             getBySeller()
    // --------------------------------------------
    @Test
    void getBySeller_returnsPageOfResponses() {
        Transaction tx = new Transaction();
        TransactionResponse response = new TransactionResponse(
                1L, 5L, new BigDecimal("50.00"),
                PaymentType.CARD, LocalDateTime.now(),
                TransactionStatus.PENDING, LocalDateTime.now()
        );

        Pageable pageable = Pageable.ofSize(5);

        when(transactionRepository.findAllBySellerId(5L, pageable))
                .thenReturn(new PageImpl<>(List.of(tx)));
        when(transactionMapper.toResponse(tx)).thenReturn(response);

        Page<TransactionResponse> result = transactionService.getBySeller(5L, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(5L, result.getContent().getFirst().sellerId());
        verify(transactionRepository).findAllBySellerId(5L, pageable);
        verify(transactionMapper).toResponse(tx);
    }

    // --------------------------------------------
    //               getById()
    // --------------------------------------------
    @Test
    void getById_returnsResponse_whenFound() {
        Transaction tx = new Transaction();
        tx.setId(10L);

        TransactionResponse response = new TransactionResponse(
                10L, 1L, new BigDecimal("123.45"),
                PaymentType.CASH, LocalDateTime.now(),
                TransactionStatus.PENDING, LocalDateTime.now()
        );

        when(transactionRepository.findById(10L)).thenReturn(Optional.of(tx));
        when(transactionMapper.toResponse(tx)).thenReturn(response);

        TransactionResponse result = transactionService.getById(10L);

        assertEquals(10L, result.id());
        verify(transactionRepository).findById(10L);
        verify(transactionMapper).toResponse(tx);
    }

    @Test
    void getById_throwsException_whenNotFound() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> transactionService.getById(99L));

        verify(transactionRepository).findById(99L);
        verifyNoInteractions(transactionMapper);
    }

    // --------------------------------------------
    //          createPending()
    // --------------------------------------------
    @Test
    void createPending_createsPendingTransaction_whenSellerExists() {
        TransactionCreateRequest request = new TransactionCreateRequest(
                7L,
                new BigDecimal("150.00"),
                PaymentType.TRANSFER
        );

        Seller seller = new Seller();
        seller.setId(7L);

        Transaction tx = new Transaction();
        Transaction saved = new Transaction();
        saved.setId(1L);
        saved.setSeller(seller);
        saved.setAmount(new BigDecimal("150.00"));
        saved.setPaymentType(PaymentType.TRANSFER);
        saved.setStatus(TransactionStatus.PENDING);

        TransactionResponse response = new TransactionResponse(
                1L, 7L, new BigDecimal("150.00"),
                PaymentType.TRANSFER, null,
                TransactionStatus.PENDING, null
        );

        when(sellerRepository.findByIdAndDeletedFalse(7L))
                .thenReturn(Optional.of(seller));
        when(transactionMapper.ToEntity(request)).thenReturn(tx);
        when(transactionRepository.save(tx)).thenReturn(saved);
        when(transactionMapper.toResponse(saved)).thenReturn(response);

        TransactionResponse result = transactionService.createPending(request);

        assertEquals(1L, result.id());
        assertEquals(7L, result.sellerId());
        assertEquals(TransactionStatus.PENDING, result.status());

        assertSame(seller, tx.getSeller());
        assertEquals(TransactionStatus.PENDING, tx.getStatus());

        verify(sellerRepository).findByIdAndDeletedFalse(7L);
        verify(transactionMapper).ToEntity(request);
        verify(transactionRepository).save(tx);
        verify(transactionMapper).toResponse(saved);
    }

    @Test
    void createPending_throwsInvalidSellerException_whenSellerNotFound() {
        TransactionCreateRequest request = new TransactionCreateRequest(
                7L,
                new BigDecimal("150.00"),
                PaymentType.TRANSFER
        );

        when(sellerRepository.findByIdAndDeletedFalse(7L))
                .thenReturn(Optional.empty());

        assertThrows(InvalidSellerException.class,
                () -> transactionService.createPending(request));

        verify(sellerRepository).findByIdAndDeletedFalse(7L);
        verifyNoInteractions(transactionMapper, transactionRepository);
    }

    // --------------------------------------------
    //              complete()
    // --------------------------------------------
    @Test
    void complete_returnsExistingResponse_whenAlreadyCompleted() {
        Transaction tx = new Transaction();
        tx.setId(10L);
        tx.setStatus(TransactionStatus.COMPLETED);

        TransactionResponse response = new TransactionResponse(
                10L, 1L, new BigDecimal("100.00"),
                PaymentType.CASH, LocalDateTime.now(),
                TransactionStatus.COMPLETED, LocalDateTime.now()
        );

        when(transactionRepository.findById(10L)).thenReturn(Optional.of(tx));
        when(transactionMapper.toResponse(tx)).thenReturn(response);

        TransactionResponse result = transactionService.complete(10L);

        assertEquals(TransactionStatus.COMPLETED, result.status());
        verify(transactionRepository).findById(10L);
        verify(transactionMapper).toResponse(tx);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void complete_completesPendingTransaction() {
        Transaction tx = new Transaction();
        tx.setId(10L);
        tx.setStatus(TransactionStatus.PENDING);

        Transaction saved = new Transaction();
        saved.setId(10L);
        saved.setStatus(TransactionStatus.COMPLETED);
        saved.setTransactionDate(LocalDateTime.now());

        TransactionResponse response = new TransactionResponse(
                10L, 1L, new BigDecimal("100.00"),
                PaymentType.CASH, saved.getTransactionDate(),
                TransactionStatus.COMPLETED, LocalDateTime.now()
        );

        when(transactionRepository.findById(10L)).thenReturn(Optional.of(tx));
        when(transactionRepository.save(tx)).thenReturn(saved);
        when(transactionMapper.toResponse(saved)).thenReturn(response);

        TransactionResponse result = transactionService.complete(10L);

        assertEquals(TransactionStatus.COMPLETED, tx.getStatus());
        assertNotNull(tx.getTransactionDate());
        assertEquals(TransactionStatus.COMPLETED, result.status());

        verify(transactionRepository).findById(10L);
        verify(transactionRepository).save(tx);
        verify(transactionMapper).toResponse(saved);
    }

    @Test
    void complete_throwsInvalidTransactionException_whenStatusIsNotPendingOrCompleted() {
        Transaction tx = new Transaction();
        tx.setId(10L);
        tx.setStatus(TransactionStatus.CANCELED); // пример «левого» статуса

        when(transactionRepository.findById(10L)).thenReturn(Optional.of(tx));

        assertThrows(InvalidTransactionException.class,
                () -> transactionService.complete(10L));

        verify(transactionRepository).findById(10L);
        verify(transactionRepository, never()).save(any());
        verifyNoInteractions(transactionMapper);
    }

    @Test
    void complete_throwsEntityNotFound_whenTransactionMissing() {
        when(transactionRepository.findById(77L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> transactionService.complete(77L));

        verify(transactionRepository).findById(77L);
        verifyNoInteractions(transactionMapper);
    }
}
package org.nikolait.crmsystem.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.nikolait.crmsystem.dto.TransactionCreateRequest;
import org.nikolait.crmsystem.dto.TransactionResponse;
import org.nikolait.crmsystem.model.Seller;
import org.nikolait.crmsystem.model.Transaction;
import org.nikolait.crmsystem.model.enums.PaymentType;
import org.nikolait.crmsystem.model.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionMapperUnitTest {

    private final TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

    // --------------------------------------------
    //            toResponse()
    // --------------------------------------------
    @Test
    void toResponse_mapsTransactionToTransactionResponse() {
        // Arrange
        Seller seller = new Seller();
        seller.setId(1L);

        Transaction transaction = new Transaction(
                1L,
                seller,
                new BigDecimal("100.50"),
                PaymentType.CASH,
                TransactionStatus.PENDING,
                LocalDateTime.of(2023, 1, 1, 12, 0),
                LocalDateTime.of(2023, 1, 1, 12, 30)
        );

        // Act
        TransactionResponse response = transactionMapper.toResponse(transaction);

        // Assert
        assertEquals(1L, response.id());
        assertEquals(Long.valueOf(1), response.sellerId());
        assertEquals(new BigDecimal("100.50"), response.amount());
        assertEquals(PaymentType.CASH, response.paymentType());
        assertEquals(TransactionStatus.PENDING, response.status());
        assertEquals(LocalDateTime.of(2023, 1, 1, 12, 0), response.createdAt());
        assertEquals(LocalDateTime.of(2023, 1, 1, 12, 30), response.transactionDate());
    }

    // --------------------------------------------
    //            toEntity()
    // --------------------------------------------
    @Test
    void toEntity_mapsTransactionCreateRequestToTransaction() {
        // Arrange
        TransactionCreateRequest request = new TransactionCreateRequest(
                1L,  // sellerId
                new BigDecimal("100.50"),  // amount
                PaymentType.CASH
        );

        // Act
        Transaction transaction = transactionMapper.ToEntity(request);

        // Assert
        assertNotNull(transaction);
        assertNull(transaction.getSeller());  // seller is not mapped here
        assertEquals(new BigDecimal("100.50"), transaction.getAmount());
        assertEquals(PaymentType.CASH, transaction.getPaymentType());
    }
}
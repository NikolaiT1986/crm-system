package org.nikolait.crmsystem.service;

import jakarta.persistence.EntityNotFoundException;
import org.nikolait.crmsystem.dto.TransactionCreateRequest;
import org.nikolait.crmsystem.dto.TransactionResponse;
import org.nikolait.crmsystem.exception.InvalidSellerException;
import org.nikolait.crmsystem.exception.InvalidTransactionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service for managing transactions.
 * Defines operations for retrieving, creating and completing transactions.
 */
public interface TransactionService {

    /**
     * Returns a page of all transactions.
     *
     * @param pageable pagination and sorting parameters; must not be {@code null}
     * @return a page of {@link TransactionResponse}
     */
    Page<TransactionResponse> getAll(Pageable pageable);

    /**
     * Returns a page of transactions for the given seller.
     *
     * <p>If the seller has no transactions or does not exist,
     * an empty page is returned.</p>
     *
     * @param sellerId identifier of the seller; must not be {@code null}
     * @param pageable pagination and sorting parameters; must not be {@code null}
     * @return a page of {@link TransactionResponse} for the given seller
     */
    Page<TransactionResponse> getBySeller(Long sellerId, Pageable pageable);

    /**
     * Returns a transaction by its identifier.
     *
     * @param id transaction identifier; must not be {@code null}
     * @return {@link TransactionResponse}
     * @throws EntityNotFoundException if transaction with the given id does not exist
     */
    TransactionResponse getById(Long id);

    /**
     * Creates a new transaction in PENDING status.
     *
     * <p>The transaction is associated with an existing, not deleted seller.
     * The {@code transactionDate} is not set at this stage and will be assigned
     * when the transaction is completed.</p>
     *
     * @param request data for creating the transaction; must not be {@code null}
     * @return created {@link TransactionResponse} in PENDING status
     * @throws InvalidSellerException if seller with {@code request.sellerId()} does not exist or is marked as deleted
     */
    TransactionResponse createPending(TransactionCreateRequest request);

    /**
     * Completes a pending transaction by its identifier.
     *
     * <p>If the transaction is already in COMPLETED status,
     * the existing transaction is returned without changes.</p>
     *
     * <p>If the transaction is in any status other than PENDING or COMPLETED,
     * an exception is thrown.</p>
     *
     * <p>On successful completion the transaction status is set to COMPLETED and
     * {@code transactionDate} is updated to the current date and time.</p>
     *
     * @param id identifier of the transaction to complete; must not be {@code null}
     * @return updated {@link TransactionResponse} in COMPLETED status
     * @throws EntityNotFoundException     if transaction with the given id does not exist
     * @throws InvalidTransactionException if the transaction has a status that cannot be completed
     */
    TransactionResponse complete(Long id);
}

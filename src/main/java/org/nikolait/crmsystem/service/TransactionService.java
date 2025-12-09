package org.nikolait.crmsystem.service;

import jakarta.persistence.EntityNotFoundException;
import org.nikolait.crmsystem.dto.TransactionCreateRequest;
import org.nikolait.crmsystem.dto.TransactionResponse;
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

    TransactionResponse createPending(TransactionCreateRequest request);

    TransactionResponse complete(Long id);
}

package org.nikolait.crmsystem.service;

import jakarta.persistence.EntityNotFoundException;
import org.nikolait.crmsystem.dto.SellerCreateRequest;
import org.nikolait.crmsystem.dto.SellerResponse;
import org.nikolait.crmsystem.dto.SellerUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service for managing sellers.
 * Defines operations for retrieving, creating, updating and soft-deleting sellers.
 */
public interface SellerService {

    /**
     * Returns a page of active (not deleted) sellers.
     *
     * @param pageable pagination and sorting parameters; must not be {@code null}
     * @return a page of {@link SellerResponse}
     */
    Page<SellerResponse> getAll(Pageable pageable);

    /**
     * Returns a seller by its identifier.
     *
     * <p>Preconditions:
     * <ul>
     *   <li>{@code id} must not be {@code null}</li>
     * </ul>
     *
     * <p>Postconditions:
     * <ul>
     *   <li>returns the seller if it exists and is not deleted</li>
     *   <li>throws {@link EntityNotFoundException} if the seller is not found</li>
     * </ul>
     *
     * @param id seller identifier
     * @return {@link SellerResponse}
     * @throws EntityNotFoundException if seller does not exist or is deleted
     */
    SellerResponse getById(Long id);

    /**
     * Creates a new seller.
     *
     * <p>The request must contain all required fields.
     * Returns the created seller.</p>
     *
     * @param request data for creating a seller; must not be {@code null}
     * @return created {@link SellerResponse}
     */
    SellerResponse create(SellerCreateRequest request);

    /**
     * Updates an existing seller.
     *
     * <p>Preconditions:
     * <ul>
     *   <li>{@code id} must refer to an existing, not deleted seller</li>
     *   <li>{@code request} may contain partial data for update (patch semantics)</li>
     * </ul>
     *
     * <p>Postconditions:
     * <ul>
     *   <li>applies non-null fields from {@code request} to the target seller</li>
     *   <li>returns the updated seller</li>
     *   <li>throws {@link EntityNotFoundException} if the seller is not found</li>
     * </ul>
     *
     * @param id      seller identifier
     * @param request partial update data
     * @return updated {@link SellerResponse}
     * @throws EntityNotFoundException if seller does not exist or is deleted
     */
    SellerResponse update(Long id, SellerUpdateRequest request);

    /**
     * Soft-deletes a seller by marking it as deleted.
     *
     * <p>Postconditions:
     * <ul>
     *   <li>if seller exists, its {@code deleted} flag becomes {@code true}</li>
     *   <li>throws {@link EntityNotFoundException} if the seller is not found</li>
     * </ul>
     *
     * @param id seller identifier
     * @throws EntityNotFoundException if seller does not exist or is already deleted
     */
    void delete(Long id);
}

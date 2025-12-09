package org.nikolait.crmsystem.repository;

import org.nikolait.crmsystem.model.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByIdAndDeletedFalse(Long id);

    Page<Seller> findAllByDeletedFalse(Pageable pageable);

}

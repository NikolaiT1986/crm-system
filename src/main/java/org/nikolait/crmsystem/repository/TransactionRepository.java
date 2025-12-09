package org.nikolait.crmsystem.repository;

import org.nikolait.crmsystem.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findAllBySellerId(Long sellerId, Pageable pageable);
}

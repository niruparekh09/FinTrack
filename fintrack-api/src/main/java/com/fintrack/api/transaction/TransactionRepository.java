package com.fintrack.api.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TransactionRepository extends
        JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    // For finding a Transaction by id and the user's id
    // To Prevent User A accessing User B transactions
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);
}

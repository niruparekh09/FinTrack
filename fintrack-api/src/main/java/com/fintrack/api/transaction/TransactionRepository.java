package com.fintrack.api.transaction;

import com.fintrack.api.summary.CategorySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends
        JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    // For finding a Transaction by id and the user's id
    // To Prevent User A accessing User B transactions
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    @Query("""
    SELECT COALESCE(SUM(t.amount), 0)
    FROM Transaction t
    WHERE t.userId = :userId
    AND t.type = 'INCOME'
    """)
    BigDecimal totalIncome(Long userId);


    @Query("""
    SELECT COALESCE(SUM(t.amount), 0)
    FROM Transaction t
    WHERE t.userId = :userId
    AND t.type = 'EXPENSE'
    """)
    BigDecimal totalExpense(Long userId);


    @Query("""
    SELECT new com.fintrack.api.summary.CategorySummary(
            t.category,
            SUM(t.amount)
    )
    FROM Transaction t
    WHERE t.userId = :userId
    AND t.type = 'EXPENSE'
    GROUP BY t.category
    """)
    List<CategorySummary> expenseByCategory(Long userId);}

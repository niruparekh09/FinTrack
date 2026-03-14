package com.fintrack.api.transaction;

import com.fintrack.api.exception.TransactionNotFoundException;
import com.fintrack.api.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper mapper;

    public TransactionResponse create(TransactionRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();

        Transaction newTransaction = mapper.toTransaction(request, userId);

        Transaction persistedTransaction = transactionRepository.save(newTransaction);

        return mapper.toTransactionResponse(persistedTransaction);
    }

    public TransactionResponse update(Long id, TransactionRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();

        Transaction transaction = transactionRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() ->
                        new TransactionNotFoundException("Transaction " + id + " not found"));

        mapper.updateTransactionFromRequest(request, transaction);

        Transaction updatedTransaction = transactionRepository.save(transaction);

        return mapper.toTransactionResponse(updatedTransaction);
    }

    public String delete(Long id) {

        Long userId = SecurityUtils.getCurrentUserId();

        Transaction transaction = transactionRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() ->
                        new TransactionNotFoundException("Transaction " + id + " not found"));

        transactionRepository.delete(transaction);

        return "Transaction " + id + " deleted";
    }

    public Page<TransactionResponse> getTransactions(
            TransactionType type,
            String category,
            Integer month,
            Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();

        Specification<Transaction> spec = TransactionSpecification.filterBy(
                userId,
                type,
                category,
                month
        );

        Page<Transaction> transactions = transactionRepository.findAll(spec, pageable);

        return transactions.map(mapper::toTransactionResponse);
    }
}

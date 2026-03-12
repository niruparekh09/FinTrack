package com.fintrack.api.transaction;

import com.fintrack.api.exception.TransactionNotFoundException;
import com.fintrack.api.security.SecurityUtils;
import com.fintrack.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    UserRepository userRepository;
    TransactionRepository transactionRepository;
    TransactionMapper mapper;

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

}

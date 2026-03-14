package com.fintrack.api.summary;

import com.fintrack.api.security.SecurityUtils;
import com.fintrack.api.transaction.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final TransactionRepository transactionRepository;

    public SummaryResponse getSummary() {
        Long userId = SecurityUtils.getCurrentUserId();

        BigDecimal income =
                transactionRepository.totalIncome(userId);

        BigDecimal expense =
                transactionRepository.totalExpense(userId);

        List<CategorySummary> categorySummary =
                transactionRepository.expenseByCategory(userId);

        BigDecimal balance = income.subtract(expense);

        return new SummaryResponse(
                income,
                expense,
                balance,
                categorySummary
        );
    }
}

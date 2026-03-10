package com.fintrack.api.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(

        Long id,
        TransactionType type,
        String category,
        BigDecimal amount,
        String description,
        LocalDate date

) {
}
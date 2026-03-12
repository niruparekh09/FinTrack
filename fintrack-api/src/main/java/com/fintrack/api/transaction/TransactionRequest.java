package com.fintrack.api.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(

        @NotNull TransactionType type,

        @NotBlank String category,

        @NotNull @Positive BigDecimal amount,

        String description,

        @NotNull LocalDate date
) {
}
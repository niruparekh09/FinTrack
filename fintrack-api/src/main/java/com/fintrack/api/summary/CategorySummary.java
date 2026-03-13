package com.fintrack.api.summary;

import java.math.BigDecimal;

public record CategorySummary(
        String category,
        BigDecimal amount
) {
}

package com.example.finly.finance.application.dtos.in;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BudgetInput(
        UUID accountId,
        String categoryName,
        BigDecimal amountLimit,
        LocalDate referenceMonth,
        Integer alertPercentage,
        Boolean active
) {
}


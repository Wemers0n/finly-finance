package com.example.finly.finance.application.dtos.in;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositInput(
        UUID accountId,
        String categoryName,
        BigDecimal value,
        String description
) {
}


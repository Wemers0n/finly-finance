package com.example.finly.finance.application.dtos.in;

import java.math.BigDecimal;
import java.util.UUID;

public record CardTransactionInput(
        UUID cardId,
        String categoryName,
        BigDecimal value,
        Integer installNumber,
        Integer totalInstallments,
        String description
) {}


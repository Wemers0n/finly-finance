package com.example.finly.finance.application.dtos.out;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionOutput(
        UUID id,
        LocalDateTime date,
        BigDecimal value,
        String description,
        String category,
        String type,
        String origin,
        String operation
) {
}

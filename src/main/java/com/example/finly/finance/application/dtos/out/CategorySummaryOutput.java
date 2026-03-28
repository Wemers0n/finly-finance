package com.example.finly.finance.application.dtos.out;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CategorySummaryOutput(
        String firstname,
        Integer totalCategories,
        List<CategoryItem> categories
) {
    public record CategoryItem(
            UUID id,
            String name,
            BigDecimal totalSpent,
            BigDecimal totalReceived
    ) {}
}

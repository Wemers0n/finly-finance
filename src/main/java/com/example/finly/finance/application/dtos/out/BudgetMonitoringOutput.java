package com.example.finly.finance.application.dtos.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BudgetMonitoringOutput(
        UUID accountId,
        LocalDate referenceMonth,
        BigDecimal totalPlanned,
        BigDecimal totalCurrentSpent,
        BigDecimal totalRemaining,
        List<BudgetItem> budgets
) {

    public record BudgetItem(
            UUID budgetId,
            UUID categoryId,
            String categoryName,
            BigDecimal plannedAmount,
            BigDecimal currentSpent,
            BigDecimal remainingAmount,
            BigDecimal usagePercentage,
            Integer alertPercentage,
            Boolean alertTriggered,
            Boolean exceeded
    ) {
    }
}

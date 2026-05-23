package com.example.finly.finance.application.dtos.out;

import java.math.BigDecimal;
import java.util.List;

public record AnnualRetrospectiveOutput(
    List<MonthlySummary> monthlySummaries
) {
    public record MonthlySummary(
        String monthName,
        int monthValue,
        BigDecimal totalCredits,
        BigDecimal totalDebits,
        BigDecimal balance
    ) {}
}

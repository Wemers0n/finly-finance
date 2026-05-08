package com.example.finly.finance.application.dtos.in;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

public record BudgetMonitoringCategoryInput(
    UUID accountId,
    String categoryName,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate referenceMonth
) {
}

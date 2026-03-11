package com.example.finly.finance.application.dtos.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record MonthlyTransactionSummaryOutput(
        UUID accountId,
        LocalDate referenceMonth,
        BigDecimal totalDebits,      // gastos totais
        BigDecimal totalCredits,     // entradas totais
        BigDecimal totalTransactionsBank,    // compras pelo banco
        BigDecimal totalTransactionsCard,    // compras no cartão
        BigDecimal monthlyBalance,
        List<TransactionItem> transactions
) {

    public record TransactionItem(
            UUID transactionId,
            LocalDateTime date,
            BigDecimal value,
            String category,
            String type,
            String origin
    ) {
    }
}

package com.example.finly.finance.application.dtos.out;

import com.example.finly.finance.domain.model.enums.EInvoiceStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public record InvoiceOutput(
        UUID id,
        UUID cardId,
        LocalDate dueDate,
        LocalDate closingDate,
        YearMonth referenceMonth,
        BigDecimal totalAmount,
        BigDecimal amountPaid,
        BigDecimal remainingAmount,
        EInvoiceStatus status,
        List<TransactionOutput> transactions
) {
}

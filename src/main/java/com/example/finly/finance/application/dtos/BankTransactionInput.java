package com.example.finly.finance.application.dtos;

import com.example.finly.finance.domain.model.enums.EBalanceOperation;
import com.example.finly.finance.domain.model.enums.EBankTransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record BankTransactionInput(
        UUID accountId,
        String categoryName,
        BigDecimal value,
        EBalanceOperation operation,
        EBankTransactionType transactionType,
        String description
) {
}

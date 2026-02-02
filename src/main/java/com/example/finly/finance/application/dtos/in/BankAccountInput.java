package com.example.finly.finance.application.dtos.in;

import com.example.finly.finance.domain.model.enums.EAccountType;

import java.math.BigDecimal;

public record BankAccountInput(
        EAccountType accountType,
        String accountName,
        BigDecimal initialBalance
) {
}

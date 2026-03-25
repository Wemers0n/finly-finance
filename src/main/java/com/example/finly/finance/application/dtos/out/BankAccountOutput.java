package com.example.finly.finance.application.dtos.out;

import com.example.finly.finance.domain.model.enums.EAccountType;
import java.math.BigDecimal;
import java.util.UUID;

public record BankAccountOutput(
        UUID id,
        String accountName,
        EAccountType accountType,
        BigDecimal currentBalance
) {
}

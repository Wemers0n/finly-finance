package com.example.finly.finance.application.dtos.in;

import com.example.finly.finance.domain.model.enums.EBrandCard;

import java.math.BigDecimal;
import java.util.UUID;

public record CreditCardInput(
        UUID bankAccountId,
        String cardName,
        EBrandCard brand,
        BigDecimal cardLimit,
        Integer closingDay,
        Integer dueDay
) {
}

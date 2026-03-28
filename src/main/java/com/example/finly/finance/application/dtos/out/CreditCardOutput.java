package com.example.finly.finance.application.dtos.out;

import com.example.finly.finance.domain.model.enums.EBrandCard;
import java.math.BigDecimal;
import java.util.UUID;

public record CreditCardOutput(
        UUID id,
        String cardName,
        EBrandCard brand,
        BigDecimal cardLimit,
        BigDecimal usedLimit,
        Integer closingDay,
        Integer dueDay
) {
}

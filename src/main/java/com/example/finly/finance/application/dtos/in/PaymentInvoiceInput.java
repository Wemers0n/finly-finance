package com.example.finly.finance.application.dtos.in;

import java.util.UUID;

public record PaymentInvoiceInput(
        UUID bankAccountId
) {
}

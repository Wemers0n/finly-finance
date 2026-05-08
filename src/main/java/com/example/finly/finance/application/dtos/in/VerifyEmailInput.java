package com.example.finly.finance.application.dtos.in;

import jakarta.validation.constraints.Email;

public record VerifyEmailInput(
    @Email(message = "Email inválido")
    String email
) {
}

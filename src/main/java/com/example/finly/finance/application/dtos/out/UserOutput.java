package com.example.finly.finance.application.dtos.out;

import java.util.UUID;

public record UserOutput(
        UUID userId,
        String firstname,
        String lastname,
        String email
) {
}

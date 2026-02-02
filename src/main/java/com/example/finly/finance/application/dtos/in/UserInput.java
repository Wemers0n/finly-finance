package com.example.finly.finance.application.dtos.in;

public record UserInput(
        String firstname,
        String lastname,
        String email,
        String password
) {
}

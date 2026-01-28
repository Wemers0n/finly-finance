package com.example.finly.finance.application.dtos;

public record UserInput(
        String firstname,
        String lastname,
        String email,
        String password
) {
}

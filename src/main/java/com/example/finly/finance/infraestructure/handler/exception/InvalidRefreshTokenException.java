package com.example.finly.finance.infraestructure.handler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidRefreshTokenException extends BusinessException {
    public InvalidRefreshTokenException() {
        super("Token de atualização inválido ou expirado");
    }
}

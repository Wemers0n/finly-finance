package com.example.finly.finance.domain.services.auth;

import com.example.finly.finance.infraestructure.handler.exception.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {

    public void validate(String password, String confirmPassword) {
        if (password == null || confirmPassword == null || !password.equals(confirmPassword)) {
            throw new BusinessException("As senhas não coincidem");
        }
        
        if (password.length() < 6) {
            throw new BusinessException("A senha deve ter pelo menos 6 caracteres");
        }
    }
}

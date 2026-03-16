package com.example.finly.finance.application.controller;

import com.example.finly.finance.application.dtos.in.LoginInput;
import com.example.finly.finance.application.dtos.in.UserInput;
import com.example.finly.finance.application.dtos.out.ResponseOutput;
import com.example.finly.finance.domain.services.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseOutput login(@RequestBody LoginInput input){
        return authService.login(input);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED) // Indica sucesso 201
    public ResponseOutput register(@RequestBody @Valid UserInput input) {
        return authService.register(input);
    }

}
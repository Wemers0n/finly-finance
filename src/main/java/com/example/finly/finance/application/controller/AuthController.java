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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseOutput register(@RequestBody @Valid UserInput input) {
        return authService.register(input);
    }

    @PostMapping("/refresh")
    public ResponseOutput refresh(@RequestParam String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
        }
    }

}
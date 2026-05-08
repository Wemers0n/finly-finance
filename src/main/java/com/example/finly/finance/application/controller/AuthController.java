package com.example.finly.finance.application.controller;

import com.example.finly.finance.application.dtos.in.*;
import com.example.finly.finance.application.dtos.out.AuthenticationResponse;
import com.example.finly.finance.domain.services.auth.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;
    private final RegisterService registerService;
    private final AuthRefreshTokenService refreshTokenService;
    private final LogoutService logoutService;
    private final ForgotPasswordService forgotPasswordService;

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginInput input){
        return loginService.login(input);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthenticationResponse register(@RequestBody @Valid UserInput input) {
        return registerService.register(input);
    }

    @PostMapping("/refresh")
    public AuthenticationResponse refresh(@RequestBody @Valid RefreshInput input) {
        return refreshTokenService.refresh(input.refreshToken());
    }

    @PostMapping("/forgot-password/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestBody @Valid VerifyEmailInput input) {
        if (forgotPasswordService.existsByEmail(input.email())) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void forgotPassword(@RequestBody @Valid ForgotPasswordInput input) {
        forgotPasswordService.forgotPassword(input);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logoutService.logout(token);
        }
    }
}
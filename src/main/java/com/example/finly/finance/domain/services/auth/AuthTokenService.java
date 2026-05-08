package com.example.finly.finance.domain.services.auth;

import com.example.finly.finance.application.dtos.out.AuthenticationResponse;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.infraestructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationResponse generateAuthResponse(User user) {
        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getFirstname(),
                user.getLastname()
        );
        String refreshToken = refreshTokenService.createRefreshToken(user);
        return new AuthenticationResponse(token, refreshToken);
    }

    @Transactional
    public AuthenticationResponse rotateRefreshToken(User user) {
        refreshTokenService.revokeAllUserTokens(user);
        return generateAuthResponse(user);
    }
}

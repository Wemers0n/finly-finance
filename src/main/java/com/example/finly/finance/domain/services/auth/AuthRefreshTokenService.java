package com.example.finly.finance.domain.services.auth;

import com.example.finly.finance.application.dtos.out.AuthenticationResponse;
import com.example.finly.finance.domain.model.RefreshToken;
import com.example.finly.finance.infraestructure.handler.exception.InvalidRefreshTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("authRefreshTokenService")
@RequiredArgsConstructor
public class AuthRefreshTokenService {

    private final RefreshTokenService refreshTokenService;
    private final AuthTokenService authTokenService;

    @Transactional
    public AuthenticationResponse refresh(String token) {
        RefreshToken refreshToken = refreshTokenService.validateAndGet(token);
        if (refreshToken == null) {
            throw new InvalidRefreshTokenException();
        }

        return authTokenService.rotateRefreshToken(refreshToken.getUser());
    }
}

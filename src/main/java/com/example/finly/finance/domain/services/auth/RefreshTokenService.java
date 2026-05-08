package com.example.finly.finance.domain.services.auth;

import com.example.finly.finance.domain.model.RefreshToken;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.repository.RefreshTokenRepository;
import com.example.finly.finance.infraestructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public String createRefreshToken(User user) {
        Instant expiresAt = Instant.now().plus(Duration.ofDays(7));
        String token = jwtService.generateRefreshToken(user.getId(), user.getEmail(), expiresAt);

        RefreshToken refreshToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .user(user)
                .token(token)
                .issuedAt(Instant.now())
                .expiresAt(expiresAt)
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    @Transactional
    public void revokeAllUserTokens(User user) {
        var tokens = refreshTokenRepository.findByUserAndRevokedFalse(user);
        tokens.forEach(t -> t.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);
    }

    public RefreshToken validateAndGet(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(t -> !t.isRevoked() && t.getExpiresAt().isAfter(Instant.now()))
                .orElse(null);
    }
}

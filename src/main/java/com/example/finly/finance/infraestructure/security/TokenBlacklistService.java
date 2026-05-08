package com.example.finly.finance.infraestructure.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.finly.finance.domain.model.RevokedToken;
import com.example.finly.finance.domain.repository.RevokedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RevokedTokenRepository revokedTokenRepository;
    private final JwtService jwtService;

    public void blacklistToken(String token) {
        DecodedJWT decodedJWT = jwtService.validateToken(token);
        if (decodedJWT != null) {
            RevokedToken revokedToken = RevokedToken.builder()
                    .id(UUID.randomUUID())
                    .token(token)
                    .revokedAt(Instant.now())
                    .expiresAt(decodedJWT.getExpiresAtAsInstant())
                    .build();
            revokedTokenRepository.save(revokedToken);
        }
    }

    public boolean isBlacklisted(String token) {
        return revokedTokenRepository.findByToken(token).isPresent();
    }
}

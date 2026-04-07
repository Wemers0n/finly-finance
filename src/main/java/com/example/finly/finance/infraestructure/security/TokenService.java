package com.example.finly.finance.infraestructure.security;

import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.finly.finance.domain.model.RefreshToken;
import com.example.finly.finance.domain.model.RevokedToken;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.repository.RefreshTokenRepository;
import com.example.finly.finance.domain.repository.RevokedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.*;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RevokedTokenRepository revokedTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${api.security.token.private-key}")
    private String privateKeyStr;

    @Value("${api.security.token.public-key}")
    private String publicKeyStr;

    public String generateToken(UUID userId, String email, String firstname, String lastname){

        try {

            Algorithm algorithm = Algorithm.RSA256(getPublicKey(), getPrivateKey());

            return JWT.create()
                    .withIssuer("finly-finance")
                    .withSubject(email)
                    .withClaim("userId", userId.toString())
                    .withClaim("firstname", firstname)
                    .withClaim("lastname", lastname)
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);

        } catch (JWTCreationException exception){
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    public String generateRefreshToken(User user) {
        try {
            Algorithm algorithm = Algorithm.RSA256(getPublicKey(), getPrivateKey());
            Instant expiresAt = Instant.now().plus(Duration.ofDays(7));
            
            String token = JWT.create()
                    .withIssuer("finly-finance")
                    .withSubject(user.getEmail())
                    .withClaim("userId", user.getId().toString())
                    .withClaim("type", "REFRESH")
                    .withExpiresAt(expiresAt)
                    .sign(algorithm);

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
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating refresh token", exception);
        }
    }

    public void blacklistToken(String token) {
        DecodedJWT decodedJWT = validateToken(token);
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

    public DecodedJWT validateToken(String token){

        try {
            if (revokedTokenRepository.findByToken(token).isPresent()) {
                return null;
            }

            Algorithm algorithm = Algorithm.RSA256(getPublicKey(), getPrivateKey());

            return JWT.require(algorithm)
                    .withIssuer("finly-finance")
                    .build()
                    .verify(token);

        } catch (JWTVerificationException exception){
            return null;
        }
    }

    public DecodedJWT validateRefreshToken(String token) {
        try {
            var refreshToken = refreshTokenRepository.findByToken(token);
            if (refreshToken.isEmpty() || refreshToken.get().isRevoked() || refreshToken.get().getExpiresAt().isBefore(Instant.now())) {
                return null;
            }

            Algorithm algorithm = Algorithm.RSA256(getPublicKey(), getPrivateKey());

            return JWT.require(algorithm)
                    .withIssuer("finly-finance")
                    .withClaim("type", "REFRESH")
                    .build()
                    .verify(token);
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    public void revokeRefreshTokens(User user) {
        var tokens = refreshTokenRepository.findByUserAndRevokedFalse(user);
        tokens.forEach(t -> t.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);
    }

    private RSAPrivateKey getPrivateKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) kf.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Error decoding private key", e);
        }
    }

    private RSAPublicKey getPublicKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Error decoding public key", e);
        }
    }

    private Instant generateExpirationDate(){

        return LocalDateTime
                .now()
                .plusHours(2)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}
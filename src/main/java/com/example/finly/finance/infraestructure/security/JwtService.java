package com.example.finly.finance.infraestructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${api.security.token.private-key}")
    private String privateKeyStr;

    @Value("${api.security.token.public-key}")
    private String publicKeyStr;

    public String generateToken(UUID userId, String email, String firstname, String lastname) {
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
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    public String generateRefreshToken(UUID userId, String email, Instant expiresAt) {
        try {
            Algorithm algorithm = Algorithm.RSA256(getPublicKey(), getPrivateKey());
            return JWT.create()
                    .withIssuer("finly-finance")
                    .withSubject(email)
                    .withClaim("userId", userId.toString())
                    .withClaim("type", "REFRESH")
                    .withExpiresAt(expiresAt)
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating refresh token", exception);
        }
    }

    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256(getPublicKey(), getPrivateKey());
            return JWT.require(algorithm)
                    .withIssuer("finly-finance")
                    .build()
                    .verify(token);
        } catch (JWTVerificationException exception) {
            return null;
        }
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

    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}

package com.example.finly.finance.domain.repository;

import com.example.finly.finance.domain.model.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, UUID> {
    Optional<RevokedToken> findByToken(String token);
}

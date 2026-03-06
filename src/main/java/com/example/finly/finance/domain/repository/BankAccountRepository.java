package com.example.finly.finance.domain.repository;

import com.example.finly.finance.domain.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {

    Optional<BankAccount> findByCreditCardsId(UUID cardId);
}

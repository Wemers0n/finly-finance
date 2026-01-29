package com.example.finly.finance.domain.repository;

import com.example.finly.finance.domain.model.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CreditCardRepository extends JpaRepository<CreditCard, UUID> {
}

package com.example.finly.finance.domain.repository;

import com.example.finly.finance.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.bankAccounts WHERE u.id = :id")
    Optional<User> findByIdWithBankAccounts(UUID id);
}

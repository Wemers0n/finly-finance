package com.example.finly.finance.domain.repository;

import com.example.finly.finance.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("""
            SELECT t
            FROM Transaction t
            WHERE t.accountId.id = :accountId
              AND t.transactionDate >= :startDate
              AND t.transactionDate < :endDate
            """)
    List<Transaction> findByAccountAndPeriod(
            @Param("accountId") UUID accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
            SELECT t
            FROM Transaction t
            WHERE t.accountId.id = :accountId
            ORDER BY t.transactionDate DESC
            """)
    List<Transaction> findByAccountId(@Param("accountId") UUID accountId);
}

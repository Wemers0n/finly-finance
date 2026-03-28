package com.example.finly.finance.domain.repository;

import com.example.finly.finance.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    @Query("""
        SELECT COALESCE(SUM(t.value), 0)
        FROM Transaction t
        WHERE t.categoryId.id = :categoryId
          AND TREAT(t AS BankTransaction).operation = com.example.finly.finance.domain.model.enums.EBalanceOperation.CREDIT
        """)
    BigDecimal sumReceivedByCategory(@Param("categoryId") UUID categoryId);

    @Query("""
        SELECT COALESCE(SUM(t.value), 0)
        FROM Transaction t
        WHERE t.categoryId.id = :categoryId
          AND (
                t.originType = com.example.finly.finance.domain.model.enums.ETransactionOriginType.CARD
                OR (
                    t.originType = com.example.finly.finance.domain.model.enums.ETransactionOriginType.BANK
                    AND TREAT(t AS BankTransaction).operation = com.example.finly.finance.domain.model.enums.EBalanceOperation.DEBIT
                )
              )
        """)
    BigDecimal sumSpentByCategory(@Param("categoryId") UUID categoryId);

    @Query("""
            SELECT COALESCE(SUM(t.value), 0)
            FROM CardTransaction t
            WHERE t.cardId.id = :cardId
            """)
    BigDecimal sumUsedLimit(@Param("cardId") UUID cardId);

    @Query("""
            SELECT COALESCE(SUM(t.value), 0)
            FROM CardTransaction t
            WHERE t.invoiceId.id = :invoiceId
            """)
    BigDecimal sumInvoiceTotal(@Param("invoiceId") UUID invoiceId);
}

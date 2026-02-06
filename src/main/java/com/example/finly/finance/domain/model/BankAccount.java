package com.example.finly.finance.domain.model;

import com.example.finly.finance.domain.model.enums.EAccountType;
import com.example.finly.finance.infraestructure.handler.exception.TransactionDeniedException;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_bank_accounts")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
public class BankAccount {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @Column(name = "account_name", nullable = false, length = 50)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 30)
    private EAccountType accountType;

    @Column(name = "current_balance", nullable = false)
    private BigDecimal currentBalance;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public BankAccount(User userId, String accountName, EAccountType accountType, BigDecimal initialBalance){
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.accountName = Objects.requireNonNull(accountName);
        this.accountType = Objects.requireNonNull(accountType);
        this.currentBalance = Objects.requireNonNullElse(initialBalance, BigDecimal.ZERO); // initialBalance != null ? initialBalance : BigDecimal.ZERO;
    }

    public void debit(BigDecimal value){
        validateValue(value);

        if (!accountType.allowsNegativeBalance() && currentBalance.subtract(value).compareTo(BigDecimal.ZERO) < 0){
            throw new TransactionDeniedException("Insufficient funds.");
        }
        this.currentBalance = this.currentBalance.subtract(value);
    }

    public void credit(BigDecimal value){
        validateValue(value);
        this.currentBalance = this.currentBalance.add(value);
    }

    private void validateValue(BigDecimal value){
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0){
            throw new TransactionDeniedException("The transaction value must be greater than zero.");
        }
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

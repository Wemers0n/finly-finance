package com.example.finly.finance.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_type_id", nullable = false)
    private AccountType accountTypeId;

    @Column(name = "current_balance", nullable = false)
    private BigDecimal currentBalance;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public BankAccount(User userId, String accountName, AccountType accountTypeId, BigDecimal initialBalance){
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.accountName = Objects.requireNonNull(accountName);
        this.accountTypeId = Objects.requireNonNull(accountTypeId);
        this.currentBalance = initialBalance != null ? initialBalance : BigDecimal.ZERO;

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void debit(BigDecimal value){
        this.currentBalance = this.currentBalance.subtract(value);
    }

    public void credit(BigDecimal value){
        this.currentBalance = this.currentBalance.add(value);
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

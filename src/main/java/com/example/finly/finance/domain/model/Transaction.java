package com.example.finly.finance.domain.model;

import com.example.finly.finance.domain.model.enums.ETransactionOriginType;
import com.example.finly.finance.domain.model.enums.ETransactionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_transactions")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
public abstract class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private BankAccount accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category categoryId;

    @Setter
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    @Setter(AccessLevel.PROTECTED)
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_origin_type", nullable = false)
    private ETransactionOriginType originType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status")
    private ETransactionStatus transactionStatus = ETransactionStatus.PENDING;

    @Setter
    @Column(name = "description")
    private String description;

    @Setter
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    public Transaction(BankAccount accountId, Category categoryId,
                       BigDecimal value,
                       String description) {
        this.accountId = Objects.requireNonNull(accountId);
        this.categoryId = Objects.requireNonNull(categoryId);
        this.value = Objects.requireNonNull(value);
        this.description = description;
    }

    public abstract String getTransactionTypeDisplayName();
    public abstract String getOperationDisplayName();

    public void markAsCompleted(){
        this.transactionStatus = ETransactionStatus.COMPLETED;
    }

    public void markAsExpired(){
        this.transactionStatus = ETransactionStatus.EXPIRED;
    }

    public void setAccountAndCategory(BankAccount account, Category category) {
        this.accountId = Objects.requireNonNull(account);
        this.categoryId = Objects.requireNonNull(category);
    }

    @PrePersist
    private void onCreate() {
        this.transactionDate = LocalDateTime.now();
    }
}

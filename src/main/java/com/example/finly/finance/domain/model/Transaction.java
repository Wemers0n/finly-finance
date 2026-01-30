package com.example.finly.finance.domain.model;

import com.example.finly.finance.domain.model.enums.ETransactionStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category categoryId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status")
    private ETransactionStatus transactionStatus = ETransactionStatus.PENDING;

    @Column(name = "description")
    private String description;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    public Transaction(Category categoryId,
                       BigDecimal value,
                       String description) {
        this.categoryId = Objects.requireNonNull(categoryId);
        this.value = Objects.requireNonNull(value);
        this.description = description;
        this.transactionDate = LocalDateTime.now();
    }
}

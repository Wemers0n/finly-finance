package com.example.finly.finance.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_budgets")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor
public class Budget {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category categoryId;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal amountLimit;

    @Column(nullable = false)
    private LocalDate referenceMonth;

    @Column(nullable = false)
    private Integer alertPercentage = 80;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Budget(User userId, Category categoryId, BigDecimal amountLimit, LocalDate referenceMonth, Integer alertPercentage, Boolean active){
        this.id = UUID.randomUUID();
        this.userId = Objects.requireNonNull(userId);
        this.categoryId = Objects.requireNonNull(categoryId);
        this.amountLimit = Objects.requireNonNull(amountLimit);
        this.referenceMonth = Objects.requireNonNull(referenceMonth);
        this.alertPercentage = alertPercentage;
        this.active = Objects.requireNonNull(active);

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}

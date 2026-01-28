package com.example.finly.finance.domain.model;

import com.example.finly.finance.domain.model.enums.EBrandCard;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_credit_cards")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class CreditCard {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private BankAccount bankAccountId;

    @Column(length = 50, nullable = false)
    private String cardName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EBrandCard brand;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal cardLimit;

    @Column(nullable = false)
    private Integer closingDay;

    @Column(nullable = false)
    private Integer dueDay;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

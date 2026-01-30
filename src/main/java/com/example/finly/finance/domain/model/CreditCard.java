package com.example.finly.finance.domain.model;

import com.example.finly.finance.domain.model.enums.EBrandCard;
import com.example.finly.finance.domain.model.enums.EInvoiceStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Entity
@Table(name = "tb_credit_cards")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class CreditCard {

    @Id
    @GeneratedValue
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

    @Column(name = "used_limit", precision = 10, scale = 2, nullable = false)
    private BigDecimal usedLimit = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer closingDay;

    @Column(nullable = false)
    private Integer dueDay;

    @OneToMany(mappedBy = "creditCardId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invoice> invoices = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public CreditCard(User userId, BankAccount bankAccountId, String cardName, EBrandCard brand, BigDecimal cardLimit, Integer closingDay, Integer dueDay){
        this.userId = Objects.requireNonNull(userId);
        this.bankAccountId = Objects.requireNonNull(bankAccountId);
        this.cardName = Objects.requireNonNull(cardName);
        this.brand = Objects.requireNonNull(brand);
        this.cardLimit = Objects.requireNonNull(cardLimit);
        this.closingDay = Objects.requireNonNull(closingDay);
        this.dueDay = Objects.requireNonNull(dueDay);
    }

    public void authorize(BigDecimal value){
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0){
            throw new RuntimeException("Valor invalido");
        }

        BigDecimal available = cardLimit.subtract(usedLimit); 
        if (available.compareTo(value) < 0){
            throw new RuntimeException("Limite do cartão insuficiente");
        }

        this.usedLimit = this.usedLimit.add(value);
    }

    public Optional<Invoice> findOpenInvoice(YearMonth referenceMonth) {
        return invoices.stream()
                .filter(i -> i.getReferenceMonth().equals(referenceMonth))
                .filter(i -> i.getStatus() == EInvoiceStatus.OPEN)
                .findFirst();
    }

    public Invoice createInvoice(YearMonth referenceMonth) {
        Invoice invoice = new Invoice(
                this,
                calculateClosingDate(referenceMonth),
                calculateDueDate(referenceMonth),
                referenceMonth
        );

        this.invoices.add(invoice);
        return invoice;
    }

    private LocalDate calculateClosingDate(YearMonth referenceMonth) {
        return referenceMonth.atDay(closingDay);
    }

    private LocalDate calculateDueDate(YearMonth referenceMonth) {
        return referenceMonth.plusMonths(1).atDay(dueDay);
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

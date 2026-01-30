package com.example.finly.finance.domain.model;

import com.example.finly.finance.domain.model.enums.EInvoiceStatus;
import jakarta.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_invoices")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Invoice {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private CreditCard creditCardId;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "closing_date", nullable = false)
    private LocalDate closingDate;

    @Column(name = "reference_month", nullable = false)
    private YearMonth referenceMonth;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "amount_paid", precision = 10, scale = 2, nullable = false)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private EInvoiceStatus status = EInvoiceStatus.OPEN;

    @OneToMany(mappedBy = "invoiceId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardTransaction> transactions = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Invoice(CreditCard creditCardId, LocalDate closingDate, LocalDate dueDate, YearMonth referenceMonth){
        this.id = UUID.randomUUID();
        this.creditCardId = Objects.requireNonNull(creditCardId);
        this.closingDate = Objects.requireNonNull(closingDate);
        this.dueDate = Objects.requireNonNull(dueDate);
        this.referenceMonth = Objects.requireNonNull(referenceMonth);
    }

    public void addChanges(BigDecimal value){
        validateValue(value);
        this.totalAmount = this.totalAmount.add(value);
    }

    public void payInvoice(BigDecimal value){
        validateValue(value);

        this.amountPaid = this.amountPaid.add(value);

        if (this.amountPaid.compareTo(this.totalAmount) >= 0) {
            this.status = EInvoiceStatus.PAID;
        }
    }

    public void closeInvoice(){
        if (this.status != EInvoiceStatus.OPEN){
            throw new RuntimeException("Fatura não pode ser fechada");
        }

        this.status = EInvoiceStatus.CLOSED;
    }

    public void addTransaction(CardTransaction transaction) {
        this.transactions.add(transaction);
        this.totalAmount = this.totalAmount.add(transaction.getValue());
    }

    private void validateValue(BigDecimal value){
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0){
            throw new RuntimeException("Valor da transação deve ser maior que zero");
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

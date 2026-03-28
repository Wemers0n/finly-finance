package com.example.finly.finance.domain.model;

import com.example.finly.finance.domain.model.enums.EInvoiceStatus;
import com.example.finly.finance.infraestructure.handler.exception.BusinessException;
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
    @GeneratedValue
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
        this.creditCardId = Objects.requireNonNull(creditCardId);
        this.closingDate = Objects.requireNonNull(closingDate);
        this.dueDate = Objects.requireNonNull(dueDate);
        this.referenceMonth = Objects.requireNonNull(referenceMonth);
    }

    public void addTransaction(CardTransaction transaction) {
        this.transactions.add(transaction);
    }

    public boolean shouldClose(LocalDate today){
        return status == EInvoiceStatus.OPEN && !today.isBefore(closingDate);
    }

    public boolean isPayable(){
        return status == EInvoiceStatus.CLOSED;
    }

    public BigDecimal getTotalAmount() {
        return transactions.stream()
                .map(CardTransaction::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal remainingAmount(){
        return getTotalAmount().subtract(amountPaid);
    }

    public void registerPayment(BigDecimal paymentAmount) {
        if (!isPayable()) {
            throw new BusinessException("A fatura não está fechada para pagamento");
        }

        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Valor do pagamento deve ser maior que zero");
        }

        BigDecimal remaining = remainingAmount();

        if (remaining.compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessException("Fatura já está totalmente paga");
        }

        if (paymentAmount.compareTo(remaining) > 0) {
            throw new BusinessException("Valor do pagamento não pode ser maior que o saldo da fatura");
        }

        this.amountPaid = this.amountPaid.add(paymentAmount);

        if (remainingAmount().compareTo(BigDecimal.ZERO) == 0) {
            this.status = EInvoiceStatus.PAID;
        }
    }

    public void closeInvoice(){
        if (this.status != EInvoiceStatus.OPEN){
            throw new BusinessException("Fatura não pode ser fechada");
        }

        this.status = EInvoiceStatus.CLOSED;
    }

    private void validateValue(BigDecimal value){
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessException("Valor da transação deve ser maior que zero");
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

package com.example.finly.finance.domain.model;

import com.example.finly.finance.domain.model.enums.EBrandCard;
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
import java.util.*;

@Entity
@Table(name = "tb_credit_cards")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class CreditCard {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User userId;

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

    @OneToMany(mappedBy = "creditCardId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invoice> invoices = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public CreditCard(BankAccount bankAccountId, String cardName, EBrandCard brand, BigDecimal cardLimit, Integer closingDay, Integer dueDay){
        this.id = UUID.randomUUID();
//        this.userId = Objects.requireNonNull(userId);
        this.bankAccountId = Objects.requireNonNull(bankAccountId);
        this.cardName = Objects.requireNonNull(cardName);
        this.brand = Objects.requireNonNull(brand);
        this.cardLimit = Objects.requireNonNull(cardLimit);
        this.closingDay = validateDay(closingDay, "fechamento");
        this.dueDay = validateDay(dueDay, "vencimento");
    }

    private Integer validateDay(Integer day, String fieldName) {
        if (day == null || day < 1 || day > 31) {
            throw new BusinessException("Dia de " + fieldName + " deve estar entre 1 e 31");
        }
        return day;
    }

    public void authorize(BigDecimal value, BigDecimal currentUsedLimit){
        validateValue(value);

        BigDecimal available = cardLimit.subtract(currentUsedLimit); 
        if (available.compareTo(value) < 0){
            throw new BusinessException("Limite do cartão insuficiente");
        }
    }

    public Invoice findOpenInvoice(YearMonth referenceMonth) {
        return invoices.stream()
                .filter(i -> i.getReferenceMonth().equals(referenceMonth))
                .filter(i -> i.getStatus() == EInvoiceStatus.OPEN)
                .findFirst()
                .orElseGet(() -> createInvoice(referenceMonth));  // Retorna a existente ou cria uma nova
    }

    public void closeInvoicesWhenNeeded(LocalDate today){
        invoices.stream()
                .filter(invoice -> invoice.shouldClose(today))
                .forEach(Invoice::closeInvoice);
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

    public YearMonth invoiceMonth(LocalDate purchaseDate){
        YearMonth month = YearMonth.from(purchaseDate);
        LocalDate closingDate = month.atDay(Math.min(closingDay, month.lengthOfMonth()));

        // Se a compra ocorreu após o fechamento, ela pertence à fatura do mês seguinte
        if (purchaseDate.isAfter(closingDate)){
            return month.plusMonths(1);
        }
        return month;
    }

    public Optional<Invoice> findInvoiceById(UUID invoiceId){
        return Optional.ofNullable(invoiceId)
                .flatMap(id -> invoices.stream()
                        .filter(invoice -> invoice.getId().equals(invoiceId))
                        .findFirst());
    }

    private void validateValue(BigDecimal value){
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessException("Valor da transação deve ser maior que zero");
        }
    }

    private LocalDate calculateClosingDate(YearMonth referenceMonth) {
        return referenceMonth.atDay(Math.min(closingDay, referenceMonth.lengthOfMonth()));
    }

    private LocalDate calculateDueDate(YearMonth referenceMonth) {
        LocalDate closingDate = calculateClosingDate(referenceMonth);
        // coloca o vencimento no mesmo mês de referência
        LocalDate dueDate = referenceMonth.atDay(Math.min(dueDay, referenceMonth.lengthOfMonth()));

        // Se o vencimento for antes ou no mesmo dia do fechamento, ele deve ser no mês seguinte
        if (!dueDate.isAfter(closingDate)) {
            YearMonth nextMonth = referenceMonth.plusMonths(1);
            dueDate = nextMonth.atDay(Math.min(dueDay, nextMonth.lengthOfMonth()));
        }

        return dueDate;
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

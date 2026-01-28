package com.example.finly.finance.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "tb_card_transactions")
@PrimaryKeyJoinColumn(name = "transaction_id")
@Getter
@NoArgsConstructor
public class CardTransaction extends Transaction{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private CreditCard cardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoiceId;

    @Column(name = "installment_number")
    private Integer installNumber = 1;

    @Column(name = "total_installments")
    private Integer totalInstallments = 1;

    public CardTransaction(CreditCard cardId, Category categoryId, BigDecimal value, Integer installNumber, Integer totalInstallments, String description){
        super(categoryId, value, description);
        this.cardId = Objects.requireNonNull(cardId);
        this.installNumber = Objects.requireNonNull(installNumber);
        this.totalInstallments = Objects.requireNonNull(totalInstallments);
    }
}

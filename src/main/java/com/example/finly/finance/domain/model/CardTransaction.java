package com.example.finly.finance.domain.model;

import com.example.finly.finance.domain.model.enums.ETransactionOriginType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Cria uma transação de cartão de crédito, incluindo parcelamento.
 *
 * Regras:
 * - Autoriza o limite do cartão antes de persistir
 * - Cria ou reutiliza a fatura aberta do mês de referência
 * - Divide o valor em parcelas com ajuste de arredondamento
 *
 * A operação é transacional: qualquer erro faz rollback automático.
 */
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

    public CardTransaction(BankAccount accountId, CreditCard cardId, Category categoryId, Invoice invoiceId, BigDecimal value, Integer installNumber, Integer totalInstallments, String description){
        super(accountId, categoryId, ETransactionOriginType.CARD, value, description);
        this.cardId = Objects.requireNonNull(cardId);
        this.invoiceId = Objects.requireNonNull(invoiceId);
        this.installNumber = Objects.requireNonNull(installNumber);
        this.totalInstallments = Objects.requireNonNull(totalInstallments);
    }

}

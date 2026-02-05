package com.example.finly.finance.domain.model;

import com.example.finly.finance.domain.model.enums.EBalanceOperation;
import com.example.finly.finance.domain.model.enums.EBankTransactionType;
import com.example.finly.finance.domain.model.enums.ETransactionOriginType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "tb_bank_transactions")
@PrimaryKeyJoinColumn(name = "transaction_id")
@Getter
@NoArgsConstructor
public class BankTransaction extends Transaction{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private BankAccount accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation", nullable = false)
    private EBalanceOperation operation;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private EBankTransactionType transactionType;

    public BankTransaction(BankAccount accountId, Category categoryId, BigDecimal value, String description, EBalanceOperation operation, EBankTransactionType transactionType){
        super(categoryId, ETransactionOriginType.BANK, value, description);
        this.accountId = Objects.requireNonNull(accountId);
        this.operation = Objects.requireNonNull(operation);
        this.transactionType = Objects.requireNonNull(transactionType);
    }
}

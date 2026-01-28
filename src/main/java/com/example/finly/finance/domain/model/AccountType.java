package com.example.finly.finance.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "tb_account_types")
@Getter
@NoArgsConstructor
public class AccountType {

    @Id
    private Integer id;

    @Column(name = "account_type", nullable = false, unique = true, length = 50)
    private String accountTypeName;

    @Column(name = "allows_negative_balance", nullable = false)
    private boolean allowsNegativeBalance;

    public boolean allowsNegativeBalance(){ // Verifica se a conta permite saldo negativo (conta corrente -> cheque especial)
        return allowsNegativeBalance;
    }
}

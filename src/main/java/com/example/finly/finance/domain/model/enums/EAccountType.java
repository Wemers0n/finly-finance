package com.example.finly.finance.domain.model.enums;

public enum EAccountType {
    CURRENT("Conta Corrente", true),
    SAVINGS("Conta Poupança", false),
    INVESTMENT("Conta Investimento", false);

    private final String label;
    private final boolean allowsNegativeBalance;

    EAccountType(String label, boolean allowsNegativeBalance) {
        this.label = label;
        this.allowsNegativeBalance = allowsNegativeBalance;
    }

    public boolean allowsNegativeBalance(){
        return allowsNegativeBalance;
    }
}

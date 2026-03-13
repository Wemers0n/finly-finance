package com.example.finly.finance.units;

import com.example.finly.finance.domain.model.BankAccount;
import com.example.finly.finance.domain.model.Budget;
import com.example.finly.finance.domain.model.Category;
import com.example.finly.finance.domain.model.Transaction;
import com.example.finly.finance.domain.model.enums.EAccountType;
import com.example.finly.finance.domain.model.enums.EBalanceOperation;
import com.example.finly.finance.domain.model.enums.EBankTransactionType;
import com.example.finly.finance.domain.services.budget.BudgetLimitValidator;
import com.example.finly.finance.infraestructure.handler.exception.TransactionDeniedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

class BudgetLimitValidatorTest {

    private final BudgetLimitValidator validator = new BudgetLimitValidator();

    @Test
    void shouldCalculateMonthlySpentConsideringOnlySameMonth() throws Exception {
        var user = new com.example.finly.finance.domain.model.User(
                "Joao", "Silva", "budget.validator@gmail.com", "12345678"
        );
        var account = new BankAccount(
                user, "Conta", EAccountType.CURRENT, BigDecimal.valueOf(1000)
        );
        Category category = new Category(account, "Alimentacao");

        Transaction t1 = new com.example.finly.finance.domain.model.BankTransaction(
                account, category, BigDecimal.valueOf(100), "Mercado",
                EBalanceOperation.DEBIT, EBankTransactionType.PIX
        );
        Transaction t2 = new com.example.finly.finance.domain.model.BankTransaction(
                account, category, BigDecimal.valueOf(50), "Padaria",
                EBalanceOperation.DEBIT, EBankTransactionType.PIX
        );

        Field dateField = Transaction.class.getDeclaredField("transactionDate");
        dateField.setAccessible(true);
        dateField.set(t1, LocalDate.of(2025, 3, 10).atStartOfDay());
        dateField.set(t2, LocalDate.of(2025, 4, 1).atStartOfDay());

        category.addTransaction(t1);
        category.addTransaction(t2);

        BigDecimal spent = validator.calculateMonthlySpent(category, YearMonth.of(2025, 3));

        Assertions.assertEquals(BigDecimal.valueOf(100), spent);
    }

    @Test
    void shouldNotThrowWhenNoActiveBudgetForMonth() {
        var user = new com.example.finly.finance.domain.model.User(
                "Joao", "Silva", "budget.validator2@gmail.com", "12345678"
        );
        var account = new BankAccount(
                user, "Conta", EAccountType.CURRENT, BigDecimal.valueOf(1000)
        );
        Category category = new Category(account, "Alimentacao");

        Budget budget = new Budget(
                account,
                category,
                BigDecimal.valueOf(200),
                LocalDate.of(2025, 4, 1),
                80,
                true
        );
        category.addBudget(budget);

        validator.validate(category, BigDecimal.valueOf(300), YearMonth.of(2025, 3));
    }

    @Test
    void shouldThrowWhenProjectedTotalExceedsAnyActiveBudget() {
        var user = new com.example.finly.finance.domain.model.User(
                "Joao", "Silva", "budget.validator3@gmail.com", "12345678"
        );
        var account = new BankAccount(
                user, "Conta", EAccountType.CURRENT, BigDecimal.valueOf(1000)
        );
        Category category = new Category(account, "Alimentacao");

        Budget budget = new Budget(
                account,
                category,
                BigDecimal.valueOf(200),
                LocalDate.of(2025, 3, 1),
                80,
                true
        );
        category.addBudget(budget);

        Assertions.assertThrows(TransactionDeniedException.class,
                () -> validator.validate(category, BigDecimal.valueOf(250), YearMonth.of(2025, 3)));
    }
}


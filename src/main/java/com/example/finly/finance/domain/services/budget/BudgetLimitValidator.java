package com.example.finly.finance.domain.services.budget;

import com.example.finly.finance.domain.model.Budget;
import com.example.finly.finance.domain.model.Category;
import com.example.finly.finance.domain.model.Transaction;
import com.example.finly.finance.domain.model.BankTransaction;
import com.example.finly.finance.domain.model.enums.EBalanceOperation;
import com.example.finly.finance.domain.model.enums.EBankTransactionType;
import com.example.finly.finance.infraestructure.handler.exception.TransactionDeniedException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Component
public class BudgetLimitValidator {

    public BigDecimal calculateMonthlySpent(Category category, YearMonth referenceMonth) {
        return category.getTransactions().stream()
                .filter(transaction -> isSameYearMonth(transaction.getTransactionDate().toLocalDate(), referenceMonth))
                .filter(this::isExpenseTransaction)
                .map(Transaction::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void validate(Category category, BigDecimal projectedTotal, YearMonth referenceMonth) {
        var activeBudgetsForMonth = category.getBudgets().stream()
                .filter(Budget::getActive)
                .filter(budget -> isSameYearMonth(budget.getReferenceMonth(), referenceMonth))
                .toList();

        if (activeBudgetsForMonth.isEmpty()) {
            return;
        }

        boolean exceeded = activeBudgetsForMonth.stream()
                .anyMatch(budget -> projectedTotal.compareTo(budget.getAmountLimit()) > 0);

        if (exceeded) {
            throw new TransactionDeniedException(
                    String.format("Transaction exceeds budget limit for category '%s' in %s",
                            category.getName(), referenceMonth)
            );
        }
    }

    private boolean isExpenseTransaction(Transaction transaction) {
        if (transaction instanceof BankTransaction) {
            BankTransaction bankTransaction = (BankTransaction) transaction;

            // Depósitos (entrada de dinheiro) não entram no orçamento
            if (bankTransaction.getTransactionType() == EBankTransactionType.DEPOSIT) {
                return false;
            }

            // Demais transações bancárias contam como despesa apenas se forem débito
            return bankTransaction.getOperation() == EBalanceOperation.DEBIT;
        }
        // Outras transações (como cartão) são sempre despesas
        return true;
    }

    private boolean isSameYearMonth(LocalDate date, YearMonth referenceMonth) {
        YearMonth yearMonth = YearMonth.from(date);
        return yearMonth.equals(referenceMonth);
    }
}


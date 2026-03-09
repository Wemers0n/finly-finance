package com.example.finly.finance.domain.services.budget;

import com.example.finly.finance.domain.model.Budget;
import com.example.finly.finance.domain.model.Category;
import com.example.finly.finance.domain.model.Transaction;
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

    private boolean isSameYearMonth(LocalDate date, YearMonth referenceMonth) {
        YearMonth yearMonth = YearMonth.from(date);
        return yearMonth.equals(referenceMonth);
    }
}


package com.example.finly.finance.domain.services.budget;

import com.example.finly.finance.application.dtos.in.BudgetInput;
import com.example.finly.finance.domain.model.BankAccount;
import com.example.finly.finance.domain.model.Budget;
import com.example.finly.finance.domain.model.Category;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.infraestructure.handler.exception.BankAccountNotFoundException;
import com.example.finly.finance.infraestructure.handler.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateBudgetService {

    private static final int DEFAULT_ALERT_PERCENTAGE = 80;

    private final BankAccountRepository bankAccountRepository;

    public UUID createBudget(BudgetInput input) {

        BankAccount account = findBankAccount(input.accountId());
        Category category = findCategory(account, input.categoryName());

        BigDecimal amountLimit = validateAmountLimit(input.amountLimit());
        LocalDate referenceMonth = normalizeReferenceMonth(input.referenceMonth());
        Integer alertPercentage = resolveAlertPercentage(input.alertPercentage());
        Boolean active = resolveActiveFlag(input.active());

        ensureNoDuplicateBudgetForMonth(category, referenceMonth);

        Budget budget = new Budget(account, category, amountLimit, referenceMonth, alertPercentage, active);

        UUID budgetId = category.addBudget(budget);

        bankAccountRepository.save(account);

        return budgetId;
    }

    private BigDecimal validateAmountLimit(BigDecimal amountLimit) {
        if (amountLimit == null || amountLimit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Budget amountLimit must be greater than zero");
        }
        return amountLimit;
    }

    private LocalDate normalizeReferenceMonth(LocalDate referenceMonth) {
        if (referenceMonth == null) {
            throw new IllegalArgumentException("Budget referenceMonth is required");
        }
        return referenceMonth.withDayOfMonth(1);
    }

    private Integer resolveAlertPercentage(Integer alertPercentage) {
        var resolved = alertPercentage != null ? alertPercentage : DEFAULT_ALERT_PERCENTAGE;
        if (resolved <= 0 || resolved > 100) {
            throw new IllegalArgumentException("Budget alertPercentage must be between 1 and 100");
        }
        return resolved;
    }

    private Boolean resolveActiveFlag(Boolean active) {
        return active != null ? active : Boolean.TRUE;
    }

    private void ensureNoDuplicateBudgetForMonth(Category category, LocalDate referenceMonth) {
        YearMonth targetMonth = YearMonth.from(referenceMonth);

        boolean hasActiveBudgetForMonth = category.getBudgets().stream()
                .filter(Budget::getActive)
                .anyMatch(budget -> YearMonth.from(budget.getReferenceMonth()).equals(targetMonth));

        if (hasActiveBudgetForMonth) {
            throw new IllegalArgumentException(String.format(
                    "Active budget already exists for category '%s' in %s",
                    category.getName(),
                    targetMonth
            ));
        }
    }

    private BankAccount findBankAccount(UUID accountId) {
        return bankAccountRepository.findById(accountId).orElseThrow(BankAccountNotFoundException::new);
    }

    private Category findCategory(BankAccount account, String categoryName) {
        return account.findCategoryByName(categoryName)
                .orElseThrow(() -> new CategoryNotFoundException("Category does not exist"));
    }
}


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

        BigDecimal amountLimit = input.amountLimit();
        LocalDate referenceMonth = input.referenceMonth();
        Integer alertPercentage = input.alertPercentage() != null ? input.alertPercentage() : DEFAULT_ALERT_PERCENTAGE;
        Boolean active = input.active() != null ? input.active() : Boolean.TRUE;

        Budget budget = new Budget(account, category, amountLimit, referenceMonth, alertPercentage, active);

        UUID budgetId = category.addBudget(budget);

        bankAccountRepository.save(account);

        return budgetId;
    }

    private BankAccount findBankAccount(UUID accountId) {
        return bankAccountRepository.findById(accountId).orElseThrow(BankAccountNotFoundException::new);
    }

    private Category findCategory(BankAccount account, String categoryName) {
        return account.findCategoryByName(categoryName)
                .orElseThrow(() -> new CategoryNotFoundException("Category does not exist"));
    }
}


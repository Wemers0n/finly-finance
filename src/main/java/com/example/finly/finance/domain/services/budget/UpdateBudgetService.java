package com.example.finly.finance.domain.services.budget;

import com.example.finly.finance.application.dtos.in.BudgetInput;
import com.example.finly.finance.domain.model.BankAccount;
import com.example.finly.finance.domain.model.Budget;
import com.example.finly.finance.domain.model.Category;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.infraestructure.handler.exception.BankAccountNotFoundException;
import com.example.finly.finance.infraestructure.handler.exception.BusinessException;
import com.example.finly.finance.infraestructure.handler.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateBudgetService {

    private final BankAccountRepository bankAccountRepository;

    public void updateBudget(UUID budgetId, BudgetInput input) {
        BankAccount account = bankAccountRepository.findById(input.accountId())
                .orElseThrow(BankAccountNotFoundException::new);

        Category category = account.findCategoryByName(input.categoryName())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        Budget budget = category.getBudgets().stream()
                .filter(b -> b.getId().equals(budgetId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Budget not found"));

        budget.update(input.amountLimit(), input.alertPercentage(), input.active());

        bankAccountRepository.save(account);
    }
}

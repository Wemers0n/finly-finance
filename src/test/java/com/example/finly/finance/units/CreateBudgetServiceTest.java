package com.example.finly.finance.units;

import com.example.finly.finance.application.dtos.in.BudgetInput;
import com.example.finly.finance.domain.model.BankAccount;
import com.example.finly.finance.domain.model.Budget;
import com.example.finly.finance.domain.model.Category;
import com.example.finly.finance.domain.model.enums.EAccountType;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.domain.services.budget.CreateBudgetService;
import com.example.finly.finance.infraestructure.handler.exception.BankAccountNotFoundException;
import com.example.finly.finance.infraestructure.handler.exception.CategoryNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CreateBudgetServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private CreateBudgetService createBudgetService;

    @Test
    void shouldCreateBudgetWhenDataIsValid() {
        UUID accountId = UUID.randomUUID();
        LocalDate referenceMonth = LocalDate.of(2025, 3, 15);

        var user = new com.example.finly.finance.domain.model.User(
                "Joao", "Silva", "test-budget@gmail.com", "12345678"
        );

        var account = new BankAccount(user, "Conta Principal", EAccountType.CURRENT, BigDecimal.valueOf(1000));
        Category category = account.addCategory("Alimentacao");

        Mockito.when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        var input = new BudgetInput(
                accountId,
                "Alimentacao",
                BigDecimal.valueOf(500),
                referenceMonth,
                80,
                true
        );

        UUID budgetId = createBudgetService.createBudget(input);

        Assertions.assertNotNull(budgetId);

        Assertions.assertEquals(1, category.getBudgets().size());

        Budget created = category.getBudgets().get(0);

        Assertions.assertEquals(BigDecimal.valueOf(500), created.getAmountLimit());
        Assertions.assertEquals(referenceMonth.withDayOfMonth(1), created.getReferenceMonth());
        Assertions.assertEquals(80, created.getAlertPercentage());
        Assertions.assertTrue(created.getActive());

        Mockito.verify(bankAccountRepository).findById(accountId);
        Mockito.verify(bankAccountRepository).save(account);
        Mockito.verifyNoMoreInteractions(bankAccountRepository);
    }


    @Test
    void shouldThrowWhenAccountNotFound() {
        UUID accountId = UUID.randomUUID();
        var input = new BudgetInput(
                accountId,
                "Alimentacao",
                BigDecimal.valueOf(500),
                LocalDate.now(),
                80,
                true
        );

        Mockito.when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(BankAccountNotFoundException.class,
                () -> createBudgetService.createBudget(input));

        Mockito.verify(bankAccountRepository).findById(accountId);
        Mockito.verifyNoMoreInteractions(bankAccountRepository);
    }

    @Test
    void shouldThrowWhenCategoryNotFoundOnAccount() {
        UUID accountId = UUID.randomUUID();
        var user = new com.example.finly.finance.domain.model.User(
                "Joao", "Silva", "test-budget2@gmail.com", "12345678"
        );
        var account = new BankAccount(user, "Conta Principal", EAccountType.CURRENT, BigDecimal.valueOf(1000));

        Mockito.when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        var input = new BudgetInput(
                accountId,
                "Alimentacao",
                BigDecimal.valueOf(500),
                LocalDate.now(),
                80,
                true
        );

        Assertions.assertThrows(CategoryNotFoundException.class,
                () -> createBudgetService.createBudget(input));

        Mockito.verify(bankAccountRepository).findById(accountId);
        Mockito.verifyNoMoreInteractions(bankAccountRepository);
    }

    @Test
    void shouldThrowWhenAmountLimitIsInvalid() {
        UUID accountId = UUID.randomUUID();
        var user = new com.example.finly.finance.domain.model.User(
                "Joao", "Silva", "test-budget3@gmail.com", "12345678"
        );
        var account = new BankAccount(user, "Conta Principal", EAccountType.CURRENT, BigDecimal.valueOf(1000));
        account.addCategory("Alimentacao");

        Mockito.when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        var input = new BudgetInput(
                accountId,
                "Alimentacao",
                BigDecimal.ZERO,
                LocalDate.now(),
                80,
                true
        );

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> createBudgetService.createBudget(input));

        Mockito.verify(bankAccountRepository).findById(accountId);
        Mockito.verifyNoMoreInteractions(bankAccountRepository);
    }

    @Test
    void shouldThrowWhenActiveBudgetAlreadyExistsForMonth() {
        UUID accountId = UUID.randomUUID();
        LocalDate ref = LocalDate.of(2025, 3, 1);

        var user = new com.example.finly.finance.domain.model.User(
                "Joao", "Silva", "test-budget4@gmail.com", "12345678"
        );
        var account = new BankAccount(user, "Conta Principal", EAccountType.CURRENT, BigDecimal.valueOf(1000));
        Category category = account.addCategory("Alimentacao");

        Budget existing = new Budget(
                account,
                category,
                BigDecimal.valueOf(300),
                ref,
                80,
                true
        );
        category.addBudget(existing);
        account.getBudgets().add(existing);

        Mockito.when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        var input = new BudgetInput(
            accountId,
            "Alimentacao",
            BigDecimal.valueOf(500),
            ref,
            80,
            true
        );

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> createBudgetService.createBudget(input));

        Mockito.verify(bankAccountRepository).findById(accountId);
        Mockito.verifyNoMoreInteractions(bankAccountRepository);
    }
}


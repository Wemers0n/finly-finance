package com.example.finly.finance.units;

import com.example.finly.finance.application.dtos.in.BankTransactionInput;
import com.example.finly.finance.domain.model.BankAccount;
import com.example.finly.finance.domain.model.Category;
import com.example.finly.finance.domain.model.enums.EAccountType;
import com.example.finly.finance.domain.model.enums.EBalanceOperation;
import com.example.finly.finance.domain.model.enums.EBankTransactionType;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.domain.services.account.CreateBankTransactionService;
import com.example.finly.finance.domain.services.budget.BudgetLimitValidator;
import com.example.finly.finance.infraestructure.handler.exception.BankAccountNotFoundException;
import com.example.finly.finance.infraestructure.handler.exception.CategoryNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CreateBankTransactionServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private BudgetLimitValidator budgetLimitValidator;

    @InjectMocks
    private CreateBankTransactionService createBankTransactionService;

    @Test
    void shouldCreateBankTransactionAndDebitAccountWhenValid() {
        UUID accountId = UUID.randomUUID();

        var user = new com.example.finly.finance.domain.model.User(
                "Joao", "Silva", "bank.tx@gmail.com", "12345678"
        );
        BankAccount account = new BankAccount(
                user, "Conta Principal", EAccountType.CURRENT, BigDecimal.valueOf(1000)
        );
        Category category = account.addCategory("Alimentacao");

        Mockito.when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        Mockito.when(budgetLimitValidator.calculateMonthlySpent(
                        Mockito.eq(category),
                        Mockito.any(YearMonth.class)))
                .thenReturn(BigDecimal.ZERO);

        Mockito.doNothing().when(budgetLimitValidator)
                .validate(Mockito.eq(category), Mockito.any(BigDecimal.class), Mockito.any(YearMonth.class));

        var input = new BankTransactionInput(
                accountId,
                "Alimentacao",
                BigDecimal.valueOf(200),
                EBalanceOperation.DEBIT,
                EBankTransactionType.PIX,
                "Mercado"
        );

        UUID txId = createBankTransactionService.bankTransaction(input);

        Assertions.assertNotNull(txId);
        Assertions.assertEquals(BigDecimal.valueOf(800), account.getCurrentBalance());
        Assertions.assertEquals(1, category.getTransactions().size());

        Mockito.verify(bankAccountRepository).findById(accountId);
        Mockito.verifyNoMoreInteractions(bankAccountRepository);
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        UUID accountId = UUID.randomUUID();
        var input = new BankTransactionInput(
                accountId,
                "Alimentacao",
                BigDecimal.valueOf(200),
                EBalanceOperation.DEBIT,
                EBankTransactionType.PIX,
                "Mercado"
        );

        Mockito.when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(BankAccountNotFoundException.class,
                () -> createBankTransactionService.bankTransaction(input));

        Mockito.verify(bankAccountRepository).findById(accountId);
        Mockito.verifyNoMoreInteractions(bankAccountRepository);
    }

    @Test
    void shouldThrowWhenCategoryDoesNotExistOnAccount() {
        UUID accountId = UUID.randomUUID();

        var user = new com.example.finly.finance.domain.model.User(
                "Joao", "Silva", "bank.tx2@gmail.com", "12345678"
        );
        BankAccount account = new BankAccount(
                user, "Conta Principal", EAccountType.CURRENT, BigDecimal.valueOf(1000)
        );

        Mockito.when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        var input = new BankTransactionInput(
                accountId,
                "Alimentacao",
                BigDecimal.valueOf(200),
                EBalanceOperation.DEBIT,
                EBankTransactionType.PIX,
                "Mercado"
        );

        Assertions.assertThrows(CategoryNotFoundException.class,
                () -> createBankTransactionService.bankTransaction(input));

        Mockito.verify(bankAccountRepository).findById(accountId);
        Mockito.verifyNoMoreInteractions(bankAccountRepository);
    }

    @Test
    void shouldCreateBankTransactionAndCreditAccountWhenCreditOperation() {
        UUID accountId = UUID.randomUUID();

        var user = new com.example.finly.finance.domain.model.User(
                "Joao", "Silva", "bank.tx3@gmail.com", "12345678"
        );
        BankAccount account = new BankAccount(
                user, "Conta Principal", EAccountType.CURRENT, BigDecimal.valueOf(1000)
        );
        Category category = account.addCategory("Alimentacao");

        Mockito.when(bankAccountRepository.findById(accountId))
                .thenReturn(Optional.of(account));

        var input = new BankTransactionInput(
                accountId,
                "Alimentacao",
                BigDecimal.valueOf(200),
                EBalanceOperation.CREDIT,
                EBankTransactionType.PIX,
                "Deposito"
        );

        UUID txId = createBankTransactionService.bankTransaction(input);

        Assertions.assertNotNull(txId);
        Assertions.assertEquals(BigDecimal.valueOf(1200), account.getCurrentBalance());
        Assertions.assertEquals(1, category.getTransactions().size());

        Mockito.verify(bankAccountRepository).findById(accountId);
        Mockito.verifyNoMoreInteractions(bankAccountRepository);
    }
}


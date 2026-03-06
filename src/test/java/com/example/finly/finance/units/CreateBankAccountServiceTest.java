package com.example.finly.finance.units;

import com.example.finly.finance.application.dtos.in.BankAccountInput;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.model.enums.EAccountType;
import com.example.finly.finance.domain.repository.UserRepository;
import com.example.finly.finance.domain.services.account.CreateBankAccountService;
import com.example.finly.finance.infraestructure.handler.exception.UserNotExistsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class CreateBankAccountServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CreateBankAccountService createBankAccountService;

    @Test
    void shouldCreateBankAccountWhenUserExistsTest(){

        UUID userId = UUID.randomUUID();
        var user = createUserTest();
        var bankAccount = new BankAccountInput(EAccountType.CURRENT, "Conta Principal", BigDecimal.valueOf(1000));

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);

        UUID result = createBankAccountService.create(userId, bankAccount);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, user.getBankAccounts().size());

        var createdAccount = user.getBankAccounts().get(0);
        Assertions.assertEquals(bankAccount.accountName(), createdAccount.getAccountName());
        Assertions.assertEquals(bankAccount.accountType(), createdAccount.getAccountType());
        Assertions.assertEquals(bankAccount.initialBalance(), createdAccount.getCurrentBalance());

        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(userRepository).save(user);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundExistsExceptionTest(){

        UUID userId = UUID.randomUUID();
        var bankAccount = new BankAccountInput(EAccountType.CURRENT, "Conta Principal", BigDecimal.valueOf(1000));

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotExistsException.class, () ->
                createBankAccountService.create(userId, bankAccount));

        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(userRepository, Mockito.never()).save(ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    private User createUserTest(){
        return new User("Joao", "Sousa", "test01@gmail.com", "12345678");
    }
}

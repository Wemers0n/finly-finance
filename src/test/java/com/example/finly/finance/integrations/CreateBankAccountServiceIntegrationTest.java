package com.example.finly.finance.integrations;

import com.example.finly.finance.application.dtos.in.BankAccountInput;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.model.enums.EAccountType;
import com.example.finly.finance.domain.repository.UserRepository;
import com.example.finly.finance.domain.services.account.CreateBankAccountService;
import com.example.finly.finance.infraestructure.handler.exception.UserNotExistsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.UUID;


public class CreateBankAccountServiceIntegrationTest extends AbstractIntegrationTest{

    @Autowired
    private CreateBankAccountService createBankAccountService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldPersistBankAccountForUserIntegrationTest(){

        var user = createUserTest();

        userRepository.save(user);

        BankAccountInput account = new BankAccountInput(EAccountType.CURRENT, "Conta Principal", BigDecimal.valueOf(500));

        UUID bankId = createBankAccountService.create(user.getId(), account);

        User updatedUser = userRepository.findByIdWithBankAccounts(user.getId()).orElseThrow();

        boolean exists = updatedUser.getBankAccounts()
                .stream().anyMatch(bankAccount -> bankAccount.getId().equals(bankId));

        Assertions.assertTrue(exists);
    }

    @Test
    void shouldThrowExceptionWhenUserNotExistsExceptionIntegrationTest(){

        UUID user = UUID.randomUUID();

        BankAccountInput account = new BankAccountInput(EAccountType.CURRENT, "Conta Principal", BigDecimal.valueOf(750));

        Assertions.assertThrows(UserNotExistsException.class, () -> createBankAccountService.create(user, account));
    }

    private User createUserTest(){
        return new User("Joao", "Sousa", "test01@gmail.com", "12345678");
    }

}

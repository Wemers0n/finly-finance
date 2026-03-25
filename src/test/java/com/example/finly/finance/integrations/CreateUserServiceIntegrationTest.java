package com.example.finly.finance.integrations;

import com.example.finly.finance.application.dtos.in.UserInput;
import com.example.finly.finance.domain.repository.UserRepository;
import com.example.finly.finance.domain.services.user.CreateUserService;
import com.example.finly.finance.infraestructure.handler.exception.EmailAlreadyRegisteredException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional // Cada teste roda dentro de uma transação -> rollback automático
public class CreateUserServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CreateUserService createUserService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateUserSuccessfullyIntegrationTest(){

        var userDTO = new UserInput("Joao", "Sousa", "test01@gmail.com", "12345678");

        var user = createUserService.create(userDTO);

        Assertions.assertNotNull(user.getId());
        Assertions.assertTrue(userRepository.existsByEmail(userDTO.email()));
    }

    @Test
    void shouldThrowIfEmailExistsIntegrationTest(){

        var userDTO = new UserInput("Joao", "Sousa", "test01@gmail.com", "12345678");

        createUserService.create(userDTO);

        Assertions.assertThrows(EmailAlreadyRegisteredException.class, () -> createUserService.create(userDTO));
    }
}

package com.example.finly.finance.integrations;

import com.example.finly.finance.application.dtos.in.UserInput;
import com.example.finly.finance.domain.repository.UserRepository;
import com.example.finly.finance.domain.services.user.CreateUserService;
import com.example.finly.finance.infraestructure.handler.exception.EmailAlreadyRegisteredException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

@SpringBootTest
@Testcontainers
@ActiveProfiles("tests")
@Transactional // Cada teste roda dentro de uma transação -> rollback automático
public class CreateUserServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Autowired
    private CreateUserService createUserService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateUserSuccessfullyIntegrationTest(){

        var userDTO = new UserInput("Joao", "Sousa", "test01@gmail.com", "12345678");

        UUID id = createUserService.create(userDTO);

        Assertions.assertNotNull(id);
        Assertions.assertTrue(userRepository.existsByEmail(userDTO.email()));
    }

    @Test
    void shouldThrowIfEmailExistsIntegrationTest(){

        var userDTO = new UserInput("Joao", "Sousa", "test01@gmail.com", "12345678");

        createUserService.create(userDTO);

        Assertions.assertThrows(EmailAlreadyRegisteredException.class, () -> createUserService.create(userDTO));
    }
}

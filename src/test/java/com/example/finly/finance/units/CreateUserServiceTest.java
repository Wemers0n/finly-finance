package com.example.finly.finance.units;

import com.example.finly.finance.application.dtos.in.UserInput;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.repository.UserRepository;
import com.example.finly.finance.domain.services.user.CreateUserService;
import com.example.finly.finance.infraestructure.handler.exception.EmailAlreadyRegisteredException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class CreateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateUserService createUserService;

    @Test
    void shouldCreateUserTest(){

        var userDTO = new UserInput("Joao", "Sousa", "test01@gmail.com", "12345678");

        Mockito.when(userRepository.existsByEmail(userDTO.email()))
                .thenReturn(false);

        Mockito.when(userRepository.save(ArgumentMatchers.any(User.class)))
                .thenAnswer(invocationOnMock -> {
                    User user = invocationOnMock.getArgument(0); // Recuperar o usuario passado no save()

                    // Simular o GeneratedValue
                    var field = User.class.getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(user, UUID.randomUUID());

                    return user; // Entidade persistida
                });

        var user = createUserService.create(userDTO);

        Assertions.assertNotNull(user.getId());
        Mockito.verify(userRepository, Mockito.times(1)).save(ArgumentMatchers.any(User.class));

    }

    @Test
    void shouldThrowIfEmailExistsTest(){

        var user = new UserInput("Joao", "Sousa", "test01@gmail.com", "12345678");

        Mockito.when(userRepository.existsByEmail(user.email())).thenReturn(true);

        Assertions.assertThrows(EmailAlreadyRegisteredException.class, () -> createUserService.create(user));

        Mockito.verify(userRepository, Mockito.never()).save(ArgumentMatchers.any(User.class));
    }

}

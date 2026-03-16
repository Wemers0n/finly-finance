package com.example.finly.finance.domain.services.user;

import com.example.finly.finance.application.dtos.in.UserInput;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.repository.UserRepository;
import com.example.finly.finance.infraestructure.handler.exception.EmailAlreadyRegisteredException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User create(UserInput input){

        if (userRepository.existsByEmail(input.email())) {
            throw new EmailAlreadyRegisteredException(input.email());
        }

        User user = new User(
                input.firstname(),
                input.lastname(),
                input.email(),
                passwordEncoder.encode(input.password())
        );

        User savedUser = this.userRepository.save(user);
        return savedUser;
    }
}

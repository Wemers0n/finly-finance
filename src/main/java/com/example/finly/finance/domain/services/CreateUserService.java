package com.example.finly.finance.domain.services;

import com.example.finly.finance.application.dtos.in.UserInput;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateUserService {

    private final UserRepository userRepository;

    public UUID create(UserInput input){

        if (userRepository.existsByEmail(input.email())) {
            throw new RuntimeException("Email já cadastrado");
        }

        User user = new User(
                input.firstname(),
                input.lastname(),
                input.email(),
                input.password()
        );

        this.userRepository.save(user);
        return user.getId();
    }
}

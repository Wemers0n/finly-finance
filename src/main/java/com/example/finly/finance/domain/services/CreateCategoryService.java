package com.example.finly.finance.domain.services;

import com.example.finly.finance.application.dtos.in.CategoryInput;
import com.example.finly.finance.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateCategoryService {

    private final UserRepository userRepository;

    public String createCategory(UUID userId, CategoryInput input){

        var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user does not exist"));

        user.addCategory(input.name());

        this.userRepository.save(user);
        return input.name();
    }
}

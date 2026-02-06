package com.example.finly.finance.domain.services;

import com.example.finly.finance.application.dtos.in.CategoryInput;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.repository.UserRepository;
import com.example.finly.finance.infraestructure.handler.exception.CategoryAlreadyExistsException;
import com.example.finly.finance.infraestructure.handler.exception.CategoryNotFoundException;
import com.example.finly.finance.infraestructure.handler.exception.UserNotExistsException;
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

        var user = validateUser(userId);
        validateCategory(user, input);

        user.addCategory(input.name());

        this.userRepository.save(user);
        return input.name();
    }

    private User validateUser(UUID userId){
        return userRepository.findById(userId).orElseThrow(UserNotExistsException::new);
    }

    private void validateCategory(User user, CategoryInput input) {
        if (user.findCategoryByName(input.name()).isPresent()) {
            throw new CategoryAlreadyExistsException();
        }
    }
}

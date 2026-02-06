package com.example.finly.finance.domain.services;

import com.example.finly.finance.application.dtos.out.CategorySummaryOutput;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.repository.UserRepository;
import com.example.finly.finance.infraestructure.handler.exception.UserNotExistsException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetCategorySummaryService {

    private final UserRepository userRepository;

    public GetCategorySummaryService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CategorySummaryOutput summaryOutput(UUID userId){
        var user = findUser(userId);

        var summary = CategorySummaryOutput.fromEntity(user);
        return summary;
    }

    private User findUser(UUID userId){
        return this.userRepository.findById(userId).orElseThrow(UserNotExistsException::new);
    }
}

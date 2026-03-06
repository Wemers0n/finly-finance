package com.example.finly.finance.domain.services.category;

import com.example.finly.finance.application.dtos.out.CategorySummaryOutput;
import com.example.finly.finance.domain.model.BankAccount;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.domain.repository.UserRepository;
import com.example.finly.finance.infraestructure.handler.exception.BankAccountNotFoundException;
import com.example.finly.finance.infraestructure.handler.exception.UserNotExistsException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetCategorySummaryService {

    private final BankAccountRepository bankAccountRepository;

    public GetCategorySummaryService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public CategorySummaryOutput summaryOutput(UUID userId){
        var user = findAccount(userId);

        var summary = CategorySummaryOutput.fromEntity(user);
        return summary;
    }

    private BankAccount findAccount(UUID accountId){
        return this.bankAccountRepository.findById(accountId).orElseThrow(BankAccountNotFoundException::new);
    }
}

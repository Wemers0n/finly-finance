package com.example.finly.finance.domain.services.category;

import com.example.finly.finance.application.dtos.in.CategoryInput;
import com.example.finly.finance.domain.model.BankAccount;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.domain.repository.UserRepository;
import com.example.finly.finance.infraestructure.handler.exception.BankAccountNotFoundException;
import com.example.finly.finance.infraestructure.handler.exception.CategoryAlreadyExistsException;
import com.example.finly.finance.infraestructure.handler.exception.UserNotExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateCategoryService {

    private final BankAccountRepository bankAccountRepository;

    public String createCategory(UUID accountId, CategoryInput input){

        var user = validateAccount(accountId);
        validateCategory(user, input);

        user.addCategory(input.name());

        this.bankAccountRepository.save(user);
        return input.name();
    }

    private BankAccount validateAccount(UUID accountId){
        return bankAccountRepository.findById(accountId).orElseThrow(BankAccountNotFoundException::new);
    }

    private void validateCategory(BankAccount account, CategoryInput input) {
        if (account.findCategoryByName(input.name()).isPresent()) {
            throw new CategoryAlreadyExistsException();
        }
    }
}

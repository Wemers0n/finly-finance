package com.example.finly.finance.domain.services.category;

import com.example.finly.finance.application.dtos.in.CategoryInput;
import com.example.finly.finance.domain.model.BankAccount;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.infraestructure.handler.exception.BankAccountNotFoundException;
import com.example.finly.finance.infraestructure.handler.exception.CategoryAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateCategoryService {

    private final BankAccountRepository bankAccountRepository;

    @CacheEvict(value = "category_summary", key = "#accountId.toString()")
    public String createCategory(UUID accountId, CategoryInput input){

        var account = validateAccount(accountId);
        validateCategory(account, input);

        account.addCategory(input.name());

        this.bankAccountRepository.save(account);
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

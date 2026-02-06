package com.example.finly.finance.domain.services;

import com.example.finly.finance.application.dtos.in.BankTransactionInput;
import com.example.finly.finance.domain.model.BankTransaction;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.model.enums.EBalanceOperation;
import com.example.finly.finance.domain.model.BankAccount;
import com.example.finly.finance.domain.model.Category;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.domain.repository.TransactionRepository;
import com.example.finly.finance.infraestructure.handler.exception.BankAccountNotFoundException;
import com.example.finly.finance.infraestructure.handler.exception.CategoryNotFoundException;
import com.example.finly.finance.infraestructure.handler.exception.TransactionDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateBankTransactionService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    public UUID bankTransaction(BankTransactionInput input){

        var account = findBankAccount(input.accountId());
        var category = findCategory(account.getUserId(), input.categoryName());

        BankTransaction transaction = new BankTransaction(account, category, input.value(), input.description(), input.operation(), input.transactionType());
        transaction.markAsCompleted();
        category.addTransaction(transaction);

        applyTransaction(account, transaction);
        this.transactionRepository.save(transaction);
        return transaction.getId();
    }

    private void applyTransaction(BankAccount account, BankTransaction transaction){
        if (!(transaction.getOperation() == EBalanceOperation.DEBIT)){
            throw new TransactionDeniedException();
        }
        account.debit(transaction.getValue());
    }

    private BankAccount findBankAccount(UUID accountId){
        return this.bankAccountRepository.findById(accountId).orElseThrow(BankAccountNotFoundException::new);
    }

    private Category findCategory(User user, String category){
        return user.findCategoryByName(category).orElseThrow(() -> new CategoryNotFoundException("Category does not exist"));
    }

}

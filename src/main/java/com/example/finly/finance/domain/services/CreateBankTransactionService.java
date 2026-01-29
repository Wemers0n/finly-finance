package com.example.finly.finance.domain.services;

import com.example.finly.finance.application.dtos.BankTransactionInput;
import com.example.finly.finance.domain.model.BankTransaction;
import com.example.finly.finance.domain.model.enums.EBalanceOperation;
import com.example.finly.finance.domain.model.BankAccount;
import com.example.finly.finance.domain.model.Category;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.domain.repository.TransactionRepository;
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

        BankAccount account = bankAccountRepository.findById(input.accountId())
                .orElseThrow(() -> new RuntimeException("Error: account does not exist"));

        Category category = account.getUserId().findCategoryByName(input.categoryName())
                .orElseThrow(() -> new RuntimeException("Error: category does not exist"));

        BankTransaction transaction = new BankTransaction(account, category, input.value(), input.description(), input.operation(), input.transactionType());
        category.addTransaction(transaction);

        applyTransaction(account, transaction);
        this.transactionRepository.save(transaction);
        return transaction.getId();
    }

    private void applyTransaction(BankAccount account, BankTransaction transaction){
        if (transaction.getOperation() == EBalanceOperation.DEBIT){
            account.debit(transaction.getValue());
        } else {
            account.credit(transaction.getValue());
        }
    }

}

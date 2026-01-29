package com.example.finly.finance.domain.services;

import com.example.finly.finance.application.dtos.CardTransactionInput;
import com.example.finly.finance.domain.model.CardTransaction;
import com.example.finly.finance.domain.model.Category;
import com.example.finly.finance.domain.model.CreditCard;
import com.example.finly.finance.domain.repository.CreditCardRepository;
import com.example.finly.finance.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateCardTransactionService {

    private final CreditCardRepository creditCardRepository;
    private final TransactionRepository transactionRepository;

    public UUID cardTransaction(CardTransactionInput input){

        CreditCard creditCard = creditCardRepository.findById(input.cardId()).orElseThrow(() -> new RuntimeException("Error: card does not exist"));

        Category category = creditCard.getUserId().findCategoryByName(input.categoryName()).orElseThrow(() -> new RuntimeException("Error: category does not exist"));

        CardTransaction transaction = new CardTransaction(creditCard, category, input.value(), input.installNumber(), input.totalInstallments(), input.description());

        category.addTransaction(transaction);
        transactionRepository.save(transaction);

        return transaction.getId();

    }
}

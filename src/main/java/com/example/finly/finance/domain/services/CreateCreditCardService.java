package com.example.finly.finance.domain.services;

import com.example.finly.finance.application.dtos.in.CreditCardInput;
import com.example.finly.finance.domain.model.BankAccount;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateCreditCardService {

    private final UserRepository userRepository;

    public UUID createCard(UUID userId, CreditCardInput input){

        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: user does not exist"));

        BankAccount account = this.validateBankAccount(user, input);

        UUID cardId = user.addCreditCard(account, input.cardName(), input.brand(), input.cardLimit(), input.closingDay(), input.dueDay());

        this.userRepository.save(user);

        return cardId;
    }

    private BankAccount validateBankAccount(User user, CreditCardInput input){
        return user.findBankAccountById(input.bankAccountId()).orElseThrow(() -> new RuntimeException("Conta Bancaria não encontrada"));
    }
}

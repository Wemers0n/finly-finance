package com.example.finly.finance.domain.services;

import com.example.finly.finance.application.dtos.in.CreditCardInput;
import com.example.finly.finance.domain.model.BankAccount;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.repository.UserRepository;
import com.example.finly.finance.infraestructure.handler.exception.BankAccountNotFoundException;
import com.example.finly.finance.infraestructure.handler.exception.UserNotExistsException;
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

        var user = findUser(userId);
        var account = this.validateBankAccount(user, input);

        UUID cardId = user.addCreditCard(account, input.cardName(), input.brand(), input.cardLimit(), input.closingDay(), input.dueDay());

        this.userRepository.save(user);

        return cardId;
    }

    private User findUser(UUID userId){
        return this.userRepository.findById(userId).orElseThrow(UserNotExistsException::new);
    }

    private BankAccount validateBankAccount(User user, CreditCardInput input){
        return user.findBankAccountById(input.bankAccountId()).orElseThrow(BankAccountNotFoundException::new);
    }
}

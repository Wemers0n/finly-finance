package com.example.finly.finance.domain.services.card;

import com.example.finly.finance.application.dtos.in.CreditCardInput;
import com.example.finly.finance.domain.model.BankAccount;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.infraestructure.handler.exception.BankAccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateCreditCardService {

    private final BankAccountRepository bankAccountRepository;

    public UUID createCard(CreditCardInput input){

//        var user = findUser(userId);
//        var account = this.validateBankAccount(user, input);
        BankAccount account = bankAccountRepository.findById(input.bankAccountId()).orElseThrow(BankAccountNotFoundException::new);

        UUID cardId = account.addCreditCard(input.cardName(), input.brand(), input.cardLimit(), input.closingDay(), input.dueDay());

        bankAccountRepository.save(account);

        return cardId;
    }
}

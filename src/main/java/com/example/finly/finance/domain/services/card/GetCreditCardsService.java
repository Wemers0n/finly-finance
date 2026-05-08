package com.example.finly.finance.domain.services.card;

import com.example.finly.finance.application.dtos.out.CreditCardOutput;
import com.example.finly.finance.application.mapper.CreditCardMapper;
import com.example.finly.finance.domain.model.BankAccount;
import com.example.finly.finance.domain.model.CreditCard;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.infraestructure.handler.exception.BankAccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCreditCardsService {

    private final BankAccountRepository bankAccountRepository;
    private final CreditCardMapper creditCardMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "account_cards", key = "#accountId.toString()")
    public List<CreditCardOutput> listByAccount(UUID accountId) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(BankAccountNotFoundException::new);

        List<CreditCard> cards = account.getCreditCards();

        return creditCardMapper.toDtoList(cards);
    }
}

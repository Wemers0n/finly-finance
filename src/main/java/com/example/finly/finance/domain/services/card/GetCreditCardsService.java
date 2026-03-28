package com.example.finly.finance.domain.services.card;

import com.example.finly.finance.application.dtos.out.CreditCardOutput;
import com.example.finly.finance.domain.model.BankAccount;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.domain.repository.TransactionRepository;
import com.example.finly.finance.infraestructure.handler.exception.BankAccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCreditCardsService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<CreditCardOutput> listByAccount(UUID accountId) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(BankAccountNotFoundException::new);

        return account.getCreditCards().stream()
                .map(card -> new CreditCardOutput(
                        card.getId(),
                        card.getCardName(),
                        card.getBrand(),
                        card.getCardLimit(),
                        transactionRepository.sumUsedLimit(card.getId()),
                        card.getClosingDay(),
                        card.getDueDay()
                ))
                .toList();
    }
}

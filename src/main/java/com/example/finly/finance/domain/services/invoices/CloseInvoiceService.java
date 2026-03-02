package com.example.finly.finance.domain.services.invoices;

import com.example.finly.finance.domain.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class CloseInvoiceService {

    private final CreditCardRepository creditCardRepository;

    public void closeInvoices(LocalDate today) {
        var cards = creditCardRepository.findCardsToCloseInvoice(today.getDayOfMonth());

        cards.forEach(card -> card.closeInvoicesWhenNeeded(today));
    }
}

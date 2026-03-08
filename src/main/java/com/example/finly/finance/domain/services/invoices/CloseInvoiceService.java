package com.example.finly.finance.domain.services.invoices;

import com.example.finly.finance.domain.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class CloseInvoiceService {

    private final CreditCardRepository creditCardRepository;
    private final Clock clock;

    public void closeInvoices() {
        LocalDate today = LocalDate.now(clock);
        var cards = creditCardRepository.findCardsToCloseInvoice(today.getDayOfMonth());

        cards.forEach(card -> card.closeInvoicesWhenNeeded(today));
    }
}

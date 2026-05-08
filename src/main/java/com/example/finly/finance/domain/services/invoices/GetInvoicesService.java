package com.example.finly.finance.domain.services.invoices;

import com.example.finly.finance.application.dtos.out.InvoiceOutput;
import com.example.finly.finance.application.mapper.InvoiceMapper;
import com.example.finly.finance.domain.model.CreditCard;
import com.example.finly.finance.domain.model.Invoice;
import com.example.finly.finance.domain.model.enums.EInvoiceStatus;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.domain.repository.CreditCardRepository;
import com.example.finly.finance.domain.repository.InvoiceRepository;
import com.example.finly.finance.infraestructure.handler.exception.BankAccountNotFoundException;
import com.example.finly.finance.infraestructure.handler.exception.BusinessException;
import com.example.finly.finance.infraestructure.handler.exception.InvoiceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetInvoicesService {

    private final InvoiceRepository invoiceRepository;
    private final CreditCardRepository creditCardRepository;
    private final BankAccountRepository bankAccountRepository;
    private final InvoiceMapper invoiceMapper;

    public List<InvoiceOutput> listByCard(UUID cardId) {
        CreditCard card = creditCardRepository.findById(cardId)
                .orElseThrow(() -> new BusinessException("Cartão não encontrado"));
        
        List<Invoice> invoices = invoiceRepository.findByCreditCardIdOrderByReferenceMonthDesc(card);
        return invoiceMapper.toDtoList(invoices);
    }

    public List<InvoiceOutput> listByAccountAndStatus(UUID accountId, EInvoiceStatus status) {
        var account = bankAccountRepository.findById(accountId)
                .orElseThrow(BankAccountNotFoundException::new);

        List<CreditCard> cards = account.getCreditCards();

        if (cards.isEmpty()) {
            return List.of();
        }

        List<Invoice> invoices = invoiceRepository.findByCreditCardIdInAndStatus(cards, status);
        return invoiceMapper.toDtoList(invoices);
    }

    public InvoiceOutput getById(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Fatura não encontrada"));
        
        return invoiceMapper.toDto(invoice);
    }
}

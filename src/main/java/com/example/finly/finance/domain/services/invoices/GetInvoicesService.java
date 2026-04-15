package com.example.finly.finance.domain.services.invoices;

import com.example.finly.finance.application.dtos.out.InvoiceOutput;
import com.example.finly.finance.application.dtos.out.TransactionOutput;
import com.example.finly.finance.domain.model.CardTransaction;
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

    public List<InvoiceOutput> listByCard(UUID cardId) {
        CreditCard card = creditCardRepository.findById(cardId)
                .orElseThrow(() -> new BusinessException("Cartão não encontrado"));
        
        return invoiceRepository.findByCreditCardIdOrderByReferenceMonthDesc(card)
                .stream()
                .map(this::toOutput)
                .toList();
    }

    public List<InvoiceOutput> listOpenInvoicesByAccount(UUID accountId) {
        var account = bankAccountRepository.findById(accountId)
                .orElseThrow(BankAccountNotFoundException::new);
        
        List<CreditCard> cards = account.getCreditCards();
        
        return invoiceRepository.findByCreditCardIdInAndStatus(cards, EInvoiceStatus.OPEN)
                .stream()
                .map(this::toOutput)
                .toList();
    }

    public InvoiceOutput getById(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Fatura não encontrada"));
        
        return toOutput(invoice);
    }

    private InvoiceOutput toOutput(Invoice invoice) {
        List<TransactionOutput> transactions = invoice.getTransactions().stream()
                .map(this::transactionToOutput)
                .toList();

        return new InvoiceOutput(
                invoice.getId(),
                invoice.getCreditCardId().getId(),
                invoice.getDueDate(),
                invoice.getClosingDate(),
                invoice.getReferenceMonth(),
                invoice.getTotalAmount(),
                invoice.getAmountPaid(),
                invoice.remainingAmount(),
                invoice.getStatus(),
                transactions
        );
    }

    private TransactionOutput transactionToOutput(CardTransaction transaction) {
        return new TransactionOutput(
                transaction.getId(),
                transaction.getTransactionDate(),
                transaction.getValue(),
                transaction.getDescription(),
                transaction.getCategoryId().getName(),
                "CREDIT_CARD",
                transaction.getOriginType().name(),
                "DEBIT"
        );
    }
}

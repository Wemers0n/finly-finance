package com.example.finly.finance.domain.services.invoices;

import com.example.finly.finance.application.dtos.in.PaymentInvoiceInput;
import com.example.finly.finance.domain.model.*;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.domain.repository.InvoiceRepository;
import com.example.finly.finance.infraestructure.handler.exception.BankAccountNotFoundException;
import com.example.finly.finance.infraestructure.handler.exception.InvoiceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentInvoiceService {

    private final BankAccountRepository bankAccountRepository;
    private final InvoiceRepository invoiceRepository;

    public void payInvoice(UUID invoiceId, PaymentInvoiceInput input){

        Invoice invoice = this.invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Fatura não existe"));

        BankAccount account = this.bankAccountRepository.findById(input.bankAccountId())
                .orElseThrow(() -> new BankAccountNotFoundException("Conta bancária não encontrada"));

        account.payCreditCardInvoice(invoice);
    }
}

package com.example.finly.finance.application.controller;


import com.example.finly.finance.application.dtos.in.PaymentInvoiceInput;
import com.example.finly.finance.application.dtos.out.InvoiceOutput;
import com.example.finly.finance.domain.services.invoices.GetInvoicesService;
import com.example.finly.finance.domain.services.invoices.PaymentInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final PaymentInvoiceService payInvoiceService;
    private final GetInvoicesService getInvoicesService;

    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<InvoiceOutput>> listByCard(@PathVariable UUID cardId) {
        return ResponseEntity.ok(getInvoicesService.listByCard(cardId));
    }

    @GetMapping("/account/{accountId}/open")
    public ResponseEntity<List<InvoiceOutput>> listOpenInvoicesByAccount(@PathVariable UUID accountId) {
        return ResponseEntity.ok(getInvoicesService.listOpenInvoicesByAccount(accountId));
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceOutput> getById(@PathVariable UUID invoiceId) {
        return ResponseEntity.ok(getInvoicesService.getById(invoiceId));
    }

    @PostMapping("/{invoiceId}/pay")
    public ResponseEntity<Void> payInvoice(@PathVariable UUID invoiceId, @RequestBody PaymentInvoiceInput input) {
        payInvoiceService.payInvoice(invoiceId, input);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
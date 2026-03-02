package com.example.finly.finance.application.controller;


import com.example.finly.finance.application.dtos.in.PaymentInvoiceInput;
import com.example.finly.finance.domain.services.invoices.PaymentInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final PaymentInvoiceService payInvoiceService;

    @PostMapping("/{invoiceId}/pay")
    public ResponseEntity<Void> payInvoice(@PathVariable UUID invoiceId, @RequestBody PaymentInvoiceInput input) {
        payInvoiceService.payInvoice(invoiceId, input);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
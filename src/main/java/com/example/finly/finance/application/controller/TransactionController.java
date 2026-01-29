package com.example.finly.finance.application.controller;

import com.example.finly.finance.application.dtos.BankTransactionInput;
import com.example.finly.finance.application.dtos.CardTransactionInput;
import com.example.finly.finance.domain.services.CreateBankTransactionService;
import com.example.finly.finance.domain.services.CreateCardTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final CreateBankTransactionService bankTransactionService;
    private final CreateCardTransactionService cardTransactionService;

    @PostMapping("/bank")
    public ResponseEntity<UUID> createBankTransaction(@RequestBody BankTransactionInput input){
        UUID transactionBankId = bankTransactionService.bankTransaction(input);
        return ResponseEntity.ok(transactionBankId);
    }

    @PostMapping("/card")
    public ResponseEntity<UUID> createCardTransaction(@RequestBody CardTransactionInput input) {
        UUID transactionCardId = cardTransactionService.cardTransaction(input);
        return ResponseEntity.ok(transactionCardId);
    }
}

package com.example.finly.finance.application.controller;

import com.example.finly.finance.application.dtos.in.BankTransactionInput;
import com.example.finly.finance.application.dtos.in.CardTransactionInput;
import com.example.finly.finance.domain.services.account.CreateBankTransactionService;
import com.example.finly.finance.domain.services.card.CreateCardTransactionService;
import com.example.finly.finance.infraestructure.utils.UriUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final CreateBankTransactionService bankTransactionService;
    private final CreateCardTransactionService cardTransactionService;

    @PostMapping("/bank")
    public ResponseEntity<Void> createBankTransaction(@RequestBody BankTransactionInput input){
        UUID transactionBankId = bankTransactionService.bankTransaction(input);
        URI locationTransactionBankUri = UriUtils.buildLocationUri(transactionBankId);

        return ResponseEntity.created(locationTransactionBankUri).build();
    }

    @PostMapping("/card")
    public ResponseEntity<Void> createCardTransaction(@RequestBody CardTransactionInput input) {
        UUID transactionCardId = cardTransactionService.cardTransaction(input);
        URI locationTransactionCardUri = UriUtils.buildLocationUri(transactionCardId);

        return ResponseEntity.created(locationTransactionCardUri).build();
    }
}

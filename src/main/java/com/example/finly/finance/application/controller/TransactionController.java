package com.example.finly.finance.application.controller;

import com.example.finly.finance.application.dtos.in.BankTransactionInput;
import com.example.finly.finance.application.dtos.in.CardTransactionInput;
import com.example.finly.finance.application.dtos.in.DepositInput;
import com.example.finly.finance.application.dtos.out.MonthlyTransactionSummaryOutput;
import com.example.finly.finance.domain.model.enums.EBalanceOperation;
import com.example.finly.finance.domain.model.enums.EBankTransactionType;
import com.example.finly.finance.domain.services.account.CreateBankTransactionService;
import com.example.finly.finance.domain.services.card.CreateCardTransactionService;
import com.example.finly.finance.domain.services.transaction.GetMonthlyTransactionSummaryService;
import com.example.finly.finance.infraestructure.utils.UriUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final CreateBankTransactionService bankTransactionService;
    private final CreateCardTransactionService cardTransactionService;
    private final GetMonthlyTransactionSummaryService getMonthlyTransactionSummaryService;

    @PostMapping("/bank")
    public ResponseEntity<Void> createBankTransaction(@RequestBody BankTransactionInput input){
        UUID transactionBankId = bankTransactionService.bankTransaction(input);
        URI locationTransactionBankUri = UriUtils.buildLocationUri(transactionBankId);

        return ResponseEntity.created(locationTransactionBankUri).build();
    }

    @PostMapping("/bank/deposit")
    public ResponseEntity<Void> createDeposit(@RequestBody DepositInput input) {
        BankTransactionInput bankInput = new BankTransactionInput(
                input.accountId(),
                input.categoryName(),
                input.value(),
                EBalanceOperation.CREDIT,
                EBankTransactionType.DEPOSIT,
                input.description()
        );

        UUID transactionBankId = bankTransactionService.bankTransaction(bankInput);
        URI locationTransactionBankUri = UriUtils.buildLocationUri(transactionBankId);

        return ResponseEntity.created(locationTransactionBankUri).build();
    }

    @PostMapping("/card")
    public ResponseEntity<Void> createCardTransaction(@RequestBody CardTransactionInput input) {
        UUID transactionCardId = cardTransactionService.cardTransaction(input);
        URI locationTransactionCardUri = UriUtils.buildLocationUri(transactionCardId);

        return ResponseEntity.created(locationTransactionCardUri).build();
    }

    @GetMapping("/summary/monthly")
    public ResponseEntity<MonthlyTransactionSummaryOutput> getMonthlySummary(
            @RequestParam UUID accountId,
            @RequestParam LocalDate referenceMonth
    ) {
        MonthlyTransactionSummaryOutput output = getMonthlyTransactionSummaryService.getSummary(accountId, referenceMonth);
        return ResponseEntity.ok(output);
    }
}

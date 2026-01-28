package com.example.finly.finance.application.controller;

import com.example.finly.finance.application.dtos.BankAccountInput;
import com.example.finly.finance.domain.services.CreateBankAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/accounts")
public class BankAccountController {

    private final CreateBankAccountService accountService;

    public BankAccountController(CreateBankAccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Void> createAccount(@PathVariable UUID userId, @RequestBody @Valid BankAccountInput input){

        this.accountService.create(userId, input);


        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

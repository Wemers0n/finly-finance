package com.example.finly.finance.application.controller;

import com.example.finly.finance.application.dtos.in.BankAccountInput;
import com.example.finly.finance.domain.services.account.CreateBankAccountService;
import com.example.finly.finance.infraestructure.utils.UriUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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

       UUID bankId = this.accountService.create(userId, input);
       URI locationBankUri = UriUtils.buildLocationUri(bankId);

        return ResponseEntity.created(locationBankUri).build();
    }
}

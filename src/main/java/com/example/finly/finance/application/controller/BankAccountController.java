package com.example.finly.finance.application.controller;

import com.example.finly.finance.application.dtos.in.BankAccountInput;
import com.example.finly.finance.application.dtos.out.BankAccountOutput;
import com.example.finly.finance.domain.services.account.CreateBankAccountService;
import com.example.finly.finance.domain.services.account.GetUserBankAccountsService;
import com.example.finly.finance.infraestructure.security.UserPrincipal;
import com.example.finly.finance.infraestructure.utils.UriUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final CreateBankAccountService accountService;
    private final GetUserBankAccountsService getUserBankAccountsService;

    @Operation(summary = "Criar conta bancária", description = "")
    @PostMapping()
    public ResponseEntity<Void> createAccount(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid BankAccountInput input){

       UUID bankId = this.accountService.create(principal.getId(), input);
       URI locationBankUri = UriUtils.buildLocationUri(bankId);

        return ResponseEntity.created(locationBankUri).build();
    }

    @Operation(summary = "Listar contas do usuário", description = "")
    @GetMapping
    public List<BankAccountOutput> listAccounts(@AuthenticationPrincipal UserPrincipal principal) {
        return getUserBankAccountsService.listAccounts(principal.getId());
    }
}

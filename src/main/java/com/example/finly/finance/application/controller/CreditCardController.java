package com.example.finly.finance.application.controller;

import com.example.finly.finance.application.dtos.in.CreditCardInput;
import com.example.finly.finance.domain.services.CreateCreditCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CreditCardController {

    private final CreateCreditCardService createCreditCardService;

    @PostMapping("/{userId}")
    public ResponseEntity<UUID> createCard(@PathVariable UUID userId, @RequestBody CreditCardInput input){
        var cardId = this.createCreditCardService.createCard(userId, input);

        return ResponseEntity.ok(cardId);
    }
}

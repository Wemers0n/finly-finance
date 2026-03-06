package com.example.finly.finance.application.controller;

import com.example.finly.finance.application.dtos.in.CreditCardInput;
import com.example.finly.finance.domain.services.card.CreateCreditCardService;
import com.example.finly.finance.infraestructure.utils.UriUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CreditCardController {

    private final CreateCreditCardService createCreditCardService;

    @PostMapping()
    public ResponseEntity<Void> createCard(@RequestBody CreditCardInput input){
        UUID cardId = this.createCreditCardService.createCard(input);

        URI locationCardUri = UriUtils.buildLocationUri(cardId);

        return ResponseEntity.created(locationCardUri).build();
    }
}

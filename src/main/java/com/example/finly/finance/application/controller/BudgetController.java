package com.example.finly.finance.application.controller;

import com.example.finly.finance.application.dtos.in.BudgetInput;
import com.example.finly.finance.domain.services.budget.CreateBudgetService;
import com.example.finly.finance.infraestructure.utils.UriUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final CreateBudgetService createBudgetService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody BudgetInput input) {
        UUID budgetId = createBudgetService.createBudget(input);

        URI locationBudgetUri = UriUtils.buildLocationUri(budgetId);

        return ResponseEntity.created(locationBudgetUri).build();
    }
}


package com.example.finly.finance.application.controller;

import com.example.finly.finance.application.dtos.in.BudgetInput;
import com.example.finly.finance.application.dtos.out.BudgetMonitoringOutput;
import com.example.finly.finance.domain.services.budget.CreateBudgetService;
import com.example.finly.finance.domain.services.budget.GetBudgetMonitoringService;
import com.example.finly.finance.infraestructure.utils.UriUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final CreateBudgetService createBudgetService;
    private final GetBudgetMonitoringService getBudgetMonitoringService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody BudgetInput input) {
        UUID budgetId = createBudgetService.createBudget(input);

        URI locationBudgetUri = UriUtils.buildLocationUri(budgetId);

        return ResponseEntity.created(locationBudgetUri).build();
    }

    @GetMapping("/monitoring")
    public ResponseEntity<BudgetMonitoringOutput> monitoring(
            @RequestParam UUID accountId,
            @RequestParam LocalDate referenceMonth
    ) {
        BudgetMonitoringOutput output = getBudgetMonitoringService.getMonitoring(accountId, referenceMonth);
        return ResponseEntity.ok(output);
    }
}


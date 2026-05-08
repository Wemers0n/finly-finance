package com.example.finly.finance.application.controller;

import com.example.finly.finance.application.dtos.in.*;
import com.example.finly.finance.application.dtos.out.BudgetMonitoringOutput;
import com.example.finly.finance.domain.services.budget.CreateBudgetService;
import com.example.finly.finance.domain.services.budget.GetBudgetMonitoringService;
import com.example.finly.finance.domain.services.budget.UpdateBudgetService;
import com.example.finly.finance.infraestructure.utils.UriUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final CreateBudgetService createBudgetService;
    private final GetBudgetMonitoringService getBudgetMonitoringService;
    private final UpdateBudgetService updateBudgetService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody BudgetInput input) {
        UUID budgetId = createBudgetService.createBudget(input);

        URI locationBudgetUri = UriUtils.buildLocationUri(budgetId);

        return ResponseEntity.created(locationBudgetUri).build();
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<Void> update(@PathVariable UUID budgetId, @RequestBody BudgetInput input) {
        updateBudgetService.updateBudget(budgetId, input);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/monitoring")
    public ResponseEntity<BudgetMonitoringOutput> monitoring(@Valid BudgetMonitoringInput input) {
        BudgetMonitoringOutput output = getBudgetMonitoringService.getMonitoring(input.accountId(), input.referenceMonth());
        return ResponseEntity.ok(output);
    }

    @GetMapping("/monitoring/category")
    public ResponseEntity<BudgetMonitoringOutput.BudgetItem> monitoringByCategory(@Valid BudgetMonitoringCategoryInput input) {
        BudgetMonitoringOutput.BudgetItem output = getBudgetMonitoringService.getMonitoringByCategory(input.accountId(), input.categoryName(), input.referenceMonth());
        return output != null ? ResponseEntity.ok(output) : ResponseEntity.notFound().build();
    }
}


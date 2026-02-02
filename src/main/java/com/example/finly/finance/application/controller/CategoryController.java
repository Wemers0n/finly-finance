package com.example.finly.finance.application.controller;

import com.example.finly.finance.application.dtos.in.CategoryInput;
import com.example.finly.finance.application.dtos.out.CategorySummaryOutput;
import com.example.finly.finance.domain.services.CreateCategoryService;
import com.example.finly.finance.domain.services.GetCategorySummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CreateCategoryService createCategoryService;
    private final GetCategorySummaryService getCategorySummaryService;

    @PostMapping("/{userId}")
    public ResponseEntity<String> create(@PathVariable UUID userId, @RequestBody CategoryInput input){

        String categoryName = createCategoryService.createCategory(userId, input);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryName);
    }

    @GetMapping("/{userId}/summary")
    public ResponseEntity<CategorySummaryOutput> categories(@PathVariable UUID userId){

        var output = getCategorySummaryService.summaryOutput(userId);

        return ResponseEntity.ok(output);
    }
}

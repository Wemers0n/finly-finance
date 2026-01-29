package com.example.finly.finance.application.controller;

import com.example.finly.finance.application.dtos.CategoryInput;
import com.example.finly.finance.domain.services.CreateCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CreateCategoryService createCategoryService;

    @PostMapping("/{userId}")
    public ResponseEntity<String> create(@PathVariable UUID userId, @RequestBody CategoryInput input){

        String categoryName = createCategoryService.createCategory(userId, input);

        return ResponseEntity.ok(categoryName);
    }
}

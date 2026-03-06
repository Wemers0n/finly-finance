package com.example.finly.finance.application.dtos.out;

import com.example.finly.finance.domain.model.BankAccount;
import com.example.finly.finance.domain.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CategorySummaryOutput(
        String firstname,
        Integer totalCategories,
        List<CategoryItem> categories
) {

    public record CategoryItem(UUID id, String name, BigDecimal totalSpent){} // Representar cada categoria na lista

    public static CategorySummaryOutput fromEntity(BankAccount account){ // Metodo Mapper
        List<CategoryItem> items = account.getCategories().stream()
                .map(category -> new CategoryItem(category.getId(), category.getName(), category.getTotalSpent()))
                .toList();

        return new CategorySummaryOutput(
                account.getUserId().getFirstname(),
                items.size(),
                items
        );
    }
}

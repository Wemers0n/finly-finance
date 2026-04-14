package com.example.finly.finance.domain.services.category;

import com.example.finly.finance.application.dtos.out.CategorySummaryOutput;
import com.example.finly.finance.domain.model.BankAccount;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.domain.repository.TransactionRepository;
import com.example.finly.finance.infraestructure.handler.exception.BankAccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCategorySummaryService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    @Cacheable(value = "category_summary", key = "#accountId.toString()")
    public CategorySummaryOutput summaryOutput(UUID accountId){
        var account = findAccount(accountId);

        List<CategorySummaryOutput.CategoryItem> items = account.getCategories().stream()
                .map(category -> new CategorySummaryOutput.CategoryItem(
                        category.getId(),
                        category.getName(),
                        transactionRepository.sumSpentByCategory(category.getId()),
                        transactionRepository.sumReceivedByCategory(category.getId())
                ))
                .toList();

        return new CategorySummaryOutput(
                account.getUserId().getFirstname(),
                items.size(),
                items
        );
    }

    private BankAccount findAccount(UUID accountId){
        return this.bankAccountRepository.findById(accountId).orElseThrow(BankAccountNotFoundException::new);
    }
}

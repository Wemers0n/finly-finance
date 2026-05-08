package com.example.finly.finance.application.mapper;

import com.example.finly.finance.application.dtos.in.BudgetInput;
import com.example.finly.finance.application.dtos.out.BudgetMonitoringOutput;
import com.example.finly.finance.domain.model.Budget;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BudgetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankAccountId", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Budget toEntity(BudgetInput input);

    @Mapping(target = "budgetId", source = "id")
    @Mapping(target = "categoryId", source = "categoryId.id")
    @Mapping(target = "categoryName", source = "categoryId.name")
    @Mapping(target = "plannedAmount", source = "amountLimit")
    @Mapping(target = "currentSpent", ignore = true)
    @Mapping(target = "remainingAmount", ignore = true)
    @Mapping(target = "usagePercentage", ignore = true)
    @Mapping(target = "alertTriggered", ignore = true)
    @Mapping(target = "exceeded", ignore = true)
    BudgetMonitoringOutput.BudgetItem toBudgetItem(Budget budget);

    List<BudgetMonitoringOutput.BudgetItem> toBudgetItemList(List<Budget> budgets);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankAccountId", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(BudgetInput input, @MappingTarget Budget budget);
}

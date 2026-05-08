package com.example.finly.finance.application.mapper;

import com.example.finly.finance.application.dtos.in.CategoryInput;
import com.example.finly.finance.application.dtos.out.CategorySummaryOutput;
import com.example.finly.finance.domain.model.Category;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankAccountId", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    Category toEntity(CategoryInput input);

    @Mapping(target = "totalSpent", ignore = true)
    @Mapping(target = "totalReceived", ignore = true)
    CategorySummaryOutput.CategoryItem toCategoryItem(Category category);

    List<CategorySummaryOutput.CategoryItem> toCategoryItemList(List<Category> categories);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankAccountId", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    void updateEntity(CategoryInput input, @MappingTarget Category category);
}

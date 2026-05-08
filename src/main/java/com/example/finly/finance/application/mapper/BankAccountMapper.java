package com.example.finly.finance.application.mapper;

import com.example.finly.finance.application.dtos.in.BankAccountInput;
import com.example.finly.finance.application.dtos.out.BankAccountOutput;
import com.example.finly.finance.domain.model.BankAccount;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BankAccountMapper {

    @Mapping(target = "monthlyBalance", ignore = true)
    BankAccountOutput toDto(BankAccount account);

    @Mapping(target = "monthlyBalance", source = "monthlyBalance")
    BankAccountOutput toDto(BankAccount account, BigDecimal monthlyBalance);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "creditCards", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BankAccount toEntity(BankAccountInput input);

    List<BankAccountOutput> toDtoList(List<BankAccount> accounts);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "currentBalance", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "creditCards", ignore = true)
    @Mapping(target = "budgets", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(BankAccountInput input, @MappingTarget BankAccount account);
}

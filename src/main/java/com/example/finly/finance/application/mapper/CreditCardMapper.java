package com.example.finly.finance.application.mapper;

import com.example.finly.finance.application.dtos.in.CreditCardInput;
import com.example.finly.finance.application.dtos.out.CreditCardOutput;
import com.example.finly.finance.domain.model.CreditCard;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreditCardMapper {

    @Mapping(target = "usedLimit", expression = "java(card.calculateUsedLimit())")
    CreditCardOutput toDto(CreditCard card);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankAccountId", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CreditCard toEntity(CreditCardInput input);

    List<CreditCardOutput> toDtoList(List<CreditCard> cards);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankAccountId", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(CreditCardInput input, @MappingTarget CreditCard card);
}

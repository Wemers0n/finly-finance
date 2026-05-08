package com.example.finly.finance.application.mapper;

import com.example.finly.finance.application.dtos.out.InvoiceOutput;
import com.example.finly.finance.domain.model.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TransactionMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvoiceMapper {

    @Mapping(target = "cardId", source = "creditCardId.id")
    @Mapping(target = "totalAmount", expression = "java(invoice.getTotalAmount())")
    @Mapping(target = "remainingAmount", expression = "java(invoice.remainingAmount())")
    InvoiceOutput toDto(Invoice invoice);

    List<InvoiceOutput> toDtoList(List<Invoice> invoices);
}

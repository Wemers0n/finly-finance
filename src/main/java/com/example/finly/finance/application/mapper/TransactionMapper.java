package com.example.finly.finance.application.mapper;

import com.example.finly.finance.application.dtos.in.BankTransactionInput;
import com.example.finly.finance.application.dtos.in.CardTransactionInput;
import com.example.finly.finance.application.dtos.out.MonthlyTransactionSummaryOutput;
import com.example.finly.finance.application.dtos.out.TransactionOutput;
import com.example.finly.finance.domain.model.BankTransaction;
import com.example.finly.finance.domain.model.CardTransaction;
import com.example.finly.finance.domain.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {

    @Mapping(target = "date", source = "transactionDate")
    @Mapping(target = "category", source = "categoryId.name")
    @Mapping(target = "type", source = "transactionTypeDisplayName")
    @Mapping(target = "origin", source = "originType")
    @Mapping(target = "operation", source = "operationDisplayName")
    TransactionOutput toDto(Transaction transaction);

    @Mapping(target = "transactionId", source = "id")
    @Mapping(target = "date", source = "transactionDate")
    @Mapping(target = "category", source = "categoryId.name")
    @Mapping(target = "type", source = "transactionTypeDisplayName")
    @Mapping(target = "origin", source = "originType")
    @Mapping(target = "operation", source = "operationDisplayName")
    MonthlyTransactionSummaryOutput.TransactionItem toSummaryItem(Transaction transaction);

    List<MonthlyTransactionSummaryOutput.TransactionItem> toSummaryItemList(List<Transaction> transactions);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "transactionStatus", ignore = true)
    @Mapping(target = "value", source = "value")
    BankTransaction toEntity(BankTransactionInput input);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "cardId", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "invoiceId", ignore = true)
    @Mapping(target = "transactionStatus", ignore = true)
    @Mapping(target = "value", source = "value")
    @Mapping(target = "installNumber", ignore = true)
    CardTransaction toEntity(CardTransactionInput input);

    List<TransactionOutput> toDtoList(List<Transaction> transactions);
}

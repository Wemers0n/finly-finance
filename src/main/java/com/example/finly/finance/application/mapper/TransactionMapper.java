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
    @Mapping(target = "type", expression = "java(determineType(transaction))")
    @Mapping(target = "origin", source = "originType")
    @Mapping(target = "operation", expression = "java(determineOperation(transaction))")
    TransactionOutput toDto(Transaction transaction);

    @Mapping(target = "transactionId", source = "id")
    @Mapping(target = "date", source = "transactionDate")
    @Mapping(target = "category", source = "categoryId.name")
    @Mapping(target = "type", expression = "java(determineType(transaction))")
    @Mapping(target = "origin", source = "originType")
    @Mapping(target = "operation", expression = "java(determineOperation(transaction))")
    MonthlyTransactionSummaryOutput.TransactionItem toSummaryItem(Transaction transaction);

    List<MonthlyTransactionSummaryOutput.TransactionItem> toSummaryItemList(List<Transaction> transactions);

    default String determineType(Transaction transaction) {
        if (transaction instanceof BankTransaction bt) {
            return bt.getTransactionType().name();
        } else if (transaction instanceof CardTransaction) {
            return "CREDIT_CARD";
        }
        return "UNKNOWN";
    }

    default String determineOperation(Transaction transaction) {
        if (transaction instanceof BankTransaction bt) {
            return bt.getOperation().name();
        } else if (transaction instanceof CardTransaction) {
            return "DEBIT";
        }
        return "UNKNOWN";
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "transactionStatus", ignore = true)
    @Mapping(target = "transactionDate", ignore = true)
    BankTransaction toEntity(BankTransactionInput input);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "cardId", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "invoiceId", ignore = true)
    @Mapping(target = "transactionStatus", ignore = true)
    @Mapping(target = "transactionDate", ignore = true)
    @Mapping(target = "installNumber", ignore = true)
    CardTransaction toEntity(CardTransactionInput input);

    List<TransactionOutput> toDtoList(List<Transaction> transactions);
}

package com.example.finly.finance.domain.services.transaction;

import com.example.finly.finance.application.dtos.out.TransactionOutput;
import com.example.finly.finance.domain.model.BankTransaction;
import com.example.finly.finance.domain.model.CardTransaction;
import com.example.finly.finance.domain.model.Transaction;
import com.example.finly.finance.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetTransactionsService {

    private final TransactionRepository transactionRepository;

    public List<TransactionOutput> listByAccount(UUID accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);

        return transactions.stream()
                .map(this::transactionOutput)
                .toList();
    }

    private TransactionOutput transactionOutput(Transaction transaction) {
        String type = "UNKNOWN";
        String operation = "DEBIT";

        if (transaction instanceof BankTransaction bankTransaction) {
            type = bankTransaction.getTransactionType() != null ? bankTransaction.getTransactionType().name() : "BANK";
            operation = bankTransaction.getOperation() != null ? bankTransaction.getOperation().name() : "DEBIT";
        } else if (transaction instanceof CardTransaction) {
            type = "CREDIT_CARD";
            operation = "DEBIT";
        }

        return new TransactionOutput(
                transaction.getId(),
                transaction.getTransactionDate(),
                transaction.getValue(),
                transaction.getDescription(),
                transaction.getCategoryId().getName(),
                type,
                transaction.getOriginType().name(),
                operation
        );
    }
}

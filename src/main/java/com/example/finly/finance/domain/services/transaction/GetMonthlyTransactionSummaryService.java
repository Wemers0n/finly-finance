package com.example.finly.finance.domain.services.transaction;

import com.example.finly.finance.application.dtos.out.MonthlyTransactionSummaryOutput;
import com.example.finly.finance.domain.model.BankTransaction;
import com.example.finly.finance.domain.model.CardTransaction;
import com.example.finly.finance.domain.model.Transaction;
import com.example.finly.finance.domain.model.enums.EBalanceOperation;
import com.example.finly.finance.domain.model.enums.EBankTransactionType;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.domain.repository.TransactionRepository;
import com.example.finly.finance.infraestructure.handler.exception.BankAccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetMonthlyTransactionSummaryService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    public MonthlyTransactionSummaryOutput getSummary(UUID accountId, LocalDate referenceMonth) {
        var account = bankAccountRepository.findById(accountId)
                .orElseThrow(BankAccountNotFoundException::new);

        YearMonth yearMonth = YearMonth.from(referenceMonth);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        // Busca no repositório todas as transações da conta dentro do período do mês
        List<Transaction> transactions = transactionRepository.findByAccountAndPeriod(
                account.getId(),
                startDate,
                endDate
        );

        BigDecimal totalTransactionsBank = sumBankPurchases(transactions);
        BigDecimal totalTransactionsCard = sumCardPurchases(transactions);

        // Soma total de débitos (banco + cartão)
        BigDecimal totalDebits = totalTransactionsBank.add(totalTransactionsCard);
        BigDecimal bankCredits = sumBankCredits(transactions);

        // Calcula o saldo do mês: créditos - débitos
        BigDecimal monthlyBalance = bankCredits.subtract(totalDebits);

        // Converte a lista de transações em itens de saída para o resumo mensal
        var transactionItems = transactions.stream()
                .map(transaction -> {
                    String categoryName = transaction.getCategoryId().getName();
                    String origin = transaction.getOriginType().name();
                    String transactionType;

                    // Caso exista um tipo específico de transação bancária, usa ele
                    if (transaction instanceof BankTransaction bankTransaction) {
                        transactionType = bankTransaction.getTransactionType() != null
                                ? bankTransaction.getTransactionType().name()
                                : "BANK";
                    } else if (transaction instanceof CardTransaction) {
                        transactionType = "CREDIT_CARD";
                    } else {
                        transactionType = "UNKNOWN";
                    }

                    return new MonthlyTransactionSummaryOutput.TransactionItem(
                            transaction.getId(),
                            transaction.getTransactionDate(),
                            transaction.getValue(),
                            categoryName,
                            transactionType,
                            origin
                    );
                })
                .toList();

        return new MonthlyTransactionSummaryOutput(
                account.getId(),
                yearMonth.atDay(1),     // mês de referência
                totalDebits,                       // total de débitos
                bankCredits,                       // total de entradas
                totalTransactionsBank,             // total de débitos bancários
                totalTransactionsCard,             // total de débitos no cartão
                monthlyBalance,                    // saldo final do mês
                transactionItems
        );
    }

    // Soma o valor de todas as transações feitas com cartão
    private BigDecimal sumCardPurchases(List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> transaction instanceof CardTransaction)
                .map(Transaction::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Soma o valor de todas as compras feitas por transações bancárias (débito)
    private BigDecimal sumBankPurchases(List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> transaction instanceof BankTransaction bankTransaction
                        && bankTransaction.getOperation() == EBalanceOperation.DEBIT)
                .map(Transaction::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Soma o valor de todos os créditos recebidos via transação bancária (inclui depósitos)
    private BigDecimal sumBankCredits(List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> transaction instanceof BankTransaction bankTransaction
                        && bankTransaction.getOperation() == EBalanceOperation.CREDIT)
                .map(Transaction::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}


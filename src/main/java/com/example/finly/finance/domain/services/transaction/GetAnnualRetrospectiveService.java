package com.example.finly.finance.domain.services.transaction;

import com.example.finly.finance.application.dtos.out.AnnualRetrospectiveOutput;
import com.example.finly.finance.domain.model.BankTransaction;
import com.example.finly.finance.domain.model.CardTransaction;
import com.example.finly.finance.domain.model.Transaction;
import com.example.finly.finance.domain.model.enums.EBalanceOperation;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.domain.repository.TransactionRepository;
import com.example.finly.finance.infraestructure.handler.exception.BankAccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAnnualRetrospectiveService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    @Cacheable(value = "annual_retrospective", key = "#accountId.toString() + '_' + #year")
    public AnnualRetrospectiveOutput getRetrospective(UUID accountId, int year) {
        if (!bankAccountRepository.existsById(accountId)) {
            throw new BankAccountNotFoundException();
        }

        List<AnnualRetrospectiveOutput.MonthlySummary> monthlySummaries = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime endDate = startDate.plusMonths(1);

            List<Transaction> transactions = transactionRepository.findByAccountAndPeriod(accountId, startDate, endDate);

            BigDecimal totalCredits = sumCredits(transactions);
            BigDecimal totalDebits = sumDebits(transactions);
            BigDecimal balance = totalCredits.subtract(totalDebits);

            String monthName = Month.of(month).getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));

            monthlySummaries.add(new AnnualRetrospectiveOutput.MonthlySummary(
                monthName,
                month,
                totalCredits,
                totalDebits,
                balance
            ));
        }

        return new AnnualRetrospectiveOutput(monthlySummaries);
    }

    private BigDecimal sumCredits(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t instanceof BankTransaction bt && bt.getOperation() == EBalanceOperation.CREDIT)
                .map(Transaction::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumDebits(List<Transaction> transactions) {
        BigDecimal bankDebits = transactions.stream()
                .filter(t -> t instanceof BankTransaction bt && bt.getOperation() == EBalanceOperation.DEBIT)
                .map(Transaction::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cardDebits = transactions.stream()
                .filter(t -> t instanceof CardTransaction)
                .map(Transaction::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return bankDebits.add(cardDebits);
    }
}

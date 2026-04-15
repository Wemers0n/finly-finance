package com.example.finly.finance.domain.services.account;

import com.example.finly.finance.application.dtos.out.BankAccountOutput;
import com.example.finly.finance.domain.model.BankTransaction;
import com.example.finly.finance.domain.model.CardTransaction;
import com.example.finly.finance.domain.model.Transaction;
import com.example.finly.finance.domain.model.enums.EBalanceOperation;
import com.example.finly.finance.domain.repository.TransactionRepository;
import com.example.finly.finance.domain.repository.UserRepository;
import com.example.finly.finance.infraestructure.handler.exception.UserNotExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserBankAccountsService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "user_accounts", key = "#userId.toString()")
    public List<BankAccountOutput> listAccounts(UUID userId) {
        var user = userRepository.findByIdWithBankAccounts(userId)
                .orElseThrow(UserNotExistsException::new);

        YearMonth yearMonth = YearMonth.now();
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        return user.findAllAccounts()
                .stream()
                .map(account -> {
                    List<Transaction> transactions = transactionRepository.findByAccountAndPeriod(
                            account.getId(),
                            startDate,
                            endDate
                    );

                   // BigDecimal currentBalance = transactionRepository.sumCurrentBalance(account.getId());
                    BigDecimal monthlyBalance = calculateMonthlyBalance(transactions);

                    return new BankAccountOutput(
                            account.getId(),
                            account.getAccountName(),
                            account.getAccountType(),
                           // currentBalance,
                            account.getCurrentBalance(),
                            monthlyBalance
                    );
                })
                .toList();
    }

    private BigDecimal calculateMonthlyBalance(List<Transaction> transactions) {
        BigDecimal credits = transactions.stream()
                .filter(t -> t instanceof BankTransaction bt && bt.getOperation() == EBalanceOperation.CREDIT)
                .map(Transaction::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal debits = transactions.stream()
                .filter(t -> (t instanceof BankTransaction bt && bt.getOperation() == EBalanceOperation.DEBIT) || t instanceof CardTransaction)
                .map(Transaction::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return credits.subtract(debits);
    }
}

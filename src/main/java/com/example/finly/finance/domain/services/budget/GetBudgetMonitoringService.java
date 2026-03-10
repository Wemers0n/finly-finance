package com.example.finly.finance.domain.services.budget;

import com.example.finly.finance.application.dtos.out.BudgetMonitoringOutput;
import com.example.finly.finance.domain.model.BankAccount;
import com.example.finly.finance.domain.model.Budget;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.infraestructure.handler.exception.BankAccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetBudgetMonitoringService {

    private final BankAccountRepository bankAccountRepository;
    private final BudgetLimitValidator budgetLimitValidator;

    public BudgetMonitoringOutput getMonitoring(UUID accountId, LocalDate referenceMonth) {
        BankAccount account = findAccount(accountId);
        YearMonth yearMonth = YearMonth.from(referenceMonth);

        List<BudgetMonitoringOutput.BudgetItem> budgetItems = buildBudgetItems(account, yearMonth);

        BigDecimal totalPlanned = sumPlannedAmount(budgetItems);
        BigDecimal totalCurrentSpent = sumCurrentSpent(budgetItems);
        BigDecimal totalRemaining = totalPlanned.subtract(totalCurrentSpent);

        return new BudgetMonitoringOutput(
                account.getId(),
                yearMonth.atDay(1),
                totalPlanned,
                totalCurrentSpent,
                totalRemaining,
                budgetItems
        );
    }

    private BankAccount findAccount(UUID accountId) {
        return bankAccountRepository.findById(accountId)
                .orElseThrow(BankAccountNotFoundException::new);
    }

    private List<BudgetMonitoringOutput.BudgetItem> buildBudgetItems(BankAccount account, YearMonth yearMonth) {
        return account.getBudgets().stream()
                .filter(Budget::getActive)
                .filter(budget -> YearMonth.from(budget.getReferenceMonth()).equals(yearMonth))
                .map(budget -> toBudgetItem(budget, yearMonth))
                .toList();
    }

    private BudgetMonitoringOutput.BudgetItem toBudgetItem(Budget budget, YearMonth referenceMonth) {
        BigDecimal currentSpent = budgetLimitValidator.calculateMonthlySpent(budget.getCategoryId(), referenceMonth);
        BigDecimal plannedAmount = budget.getAmountLimit();
        BigDecimal remainingAmount = plannedAmount.subtract(currentSpent);
        BigDecimal usagePercentage = calculateUsagePercentage(currentSpent, plannedAmount);

        boolean alertTriggered = usagePercentage.compareTo(BigDecimal.valueOf(budget.getAlertPercentage())) >= 0;
        boolean exceeded = currentSpent.compareTo(plannedAmount) > 0;

        return new BudgetMonitoringOutput.BudgetItem(
                budget.getId(),
                budget.getCategoryId().getId(),
                budget.getCategoryId().getName(),
                plannedAmount,
                currentSpent,
                remainingAmount,
                usagePercentage,
                budget.getAlertPercentage(),
                alertTriggered,
                exceeded
        );
    }

    private BigDecimal sumPlannedAmount(List<BudgetMonitoringOutput.BudgetItem> budgetItems) {
        return budgetItems.stream()
                .map(BudgetMonitoringOutput.BudgetItem::plannedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumCurrentSpent(List<BudgetMonitoringOutput.BudgetItem> budgetItems) {
        return budgetItems.stream()
                .map(BudgetMonitoringOutput.BudgetItem::currentSpent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateUsagePercentage(BigDecimal currentSpent, BigDecimal plannedAmount) {
        if (plannedAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return currentSpent
                .multiply(BigDecimal.valueOf(100))
                .divide(plannedAmount, 2, RoundingMode.HALF_UP);
    }
}

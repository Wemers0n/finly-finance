package com.example.finly.finance.domain.services.card;

import com.example.finly.finance.application.dtos.in.CardTransactionInput;
import com.example.finly.finance.domain.model.*;
import com.example.finly.finance.domain.repository.BankAccountRepository;
import com.example.finly.finance.domain.repository.TransactionRepository;
import com.example.finly.finance.domain.services.budget.BudgetLimitValidator;
import com.example.finly.finance.infraestructure.handler.exception.CategoryNotFoundException;
import com.example.finly.finance.infraestructure.handler.exception.CreditCardNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateCardTransactionService {

    private final BankAccountRepository bankAccountRepository;
    private final BudgetLimitValidator budgetLimitValidator;
    private final TransactionRepository transactionRepository;

    public UUID cardTransaction(CardTransactionInput input) {

        BankAccount account = bankAccountRepository
                .findByCreditCardsId(input.cardId())
                .orElseThrow(CreditCardNotFoundException::new);

        CreditCard creditCard = account.findCardById(input.cardId())
                .orElseThrow(CreditCardNotFoundException::new);

        Category category = account.findCategoryByName(input.categoryName())
                .orElseThrow(CategoryNotFoundException::new);

        var referenceMonth = YearMonth.from(LocalDate.now());
        var alreadySpent = budgetLimitValidator.calculateMonthlySpent(category, referenceMonth);
        var projectedTotal = alreadySpent.add(input.value());
        budgetLimitValidator.validate(category, projectedTotal, referenceMonth);

        BigDecimal currentUsedLimit = transactionRepository.sumUsedLimit(creditCard.getId());
        creditCard.authorize(input.value(), currentUsedLimit);

        // Recurso de cascade do jpa com o transactional
        return createInstallments(creditCard, category, input, LocalDate.now());
    }

    private UUID createInstallments(
            CreditCard creditCard,
            Category category,
            CardTransactionInput input,
            LocalDate purchaseDate
    ) {
        YearMonth baseInvoiceMonth = creditCard.invoiceMonth(purchaseDate);

        BigDecimal totalValue = input.value();
        int totalInstallments = input.totalInstallments();

        BigDecimal installmentsCount = BigDecimal.valueOf(totalInstallments);

        // Valor base de cada parcela com arredondamento
        BigDecimal baseInstallmentValue = totalValue
                .divide(installmentsCount, 2, RoundingMode.HALF_EVEN);

        // Qualquer resto de arredondamento é adicionado à primeira parcela
        BigDecimal remainder = totalValue.subtract(
                baseInstallmentValue.multiply(installmentsCount)
        );

        UUID lastTransactionId = null;

        for (int installmentNumber = 1; installmentNumber <= totalInstallments; installmentNumber++) {

            BigDecimal currentInstallmentValue = (installmentNumber == 1)
                    ? baseInstallmentValue.add(remainder)
                    : baseInstallmentValue;

            YearMonth invoiceMonth = baseInvoiceMonth.plusMonths(installmentNumber - 1);

            Invoice invoice = creditCard.findOpenInvoice(invoiceMonth);

            CardTransaction installmentTransaction = new CardTransaction(
                    creditCard.getBankAccountId(),
                    creditCard,
                    category,
                    invoice,
                    currentInstallmentValue,
                    installmentNumber,
                    totalInstallments,
                    input.description()
            );

            invoice.addTransaction(installmentTransaction);
            category.addTransaction(installmentTransaction);

            lastTransactionId = installmentTransaction.getId();
        }

        return lastTransactionId;
    }
}
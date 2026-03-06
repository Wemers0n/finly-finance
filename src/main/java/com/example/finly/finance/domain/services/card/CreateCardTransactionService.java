package com.example.finly.finance.domain.services.card;

import com.example.finly.finance.application.dtos.in.CardTransactionInput;
import com.example.finly.finance.domain.model.*;
import com.example.finly.finance.domain.repository.BankAccountRepository;
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

    public UUID cardTransaction(CardTransactionInput input){

        BankAccount account = bankAccountRepository
                .findByCreditCardsId(input.cardId())
                .orElseThrow(CreditCardNotFoundException::new);

        CreditCard creditCard = account.findCardById(input.cardId())
                .orElseThrow(CreditCardNotFoundException::new);

        Category category = account.findCategoryByName(input.categoryName())
                .orElseThrow(CategoryNotFoundException::new);

        creditCard.authorize(input.value());

        var id = createInstallments(creditCard, category, input);

//        this.bankAccountRepository.save(account);
        
        return id;
    }

    private UUID createInstallments(CreditCard creditCard, Category category, CardTransactionInput input){

        // Resolve o mês da fatura base considerando o dia de fechamento do cartão.
        // Ex:
        // - compra antes do fechamento → fatura do mês atual
        // - compra após o fechamento → próxima fatura
        LocalDate purchaseDate = LocalDate.now();
        YearMonth baseInvoiceMonth = creditCard.invoiceMonth(purchaseDate);

        BigDecimal totalValue = input.value();
        Integer totalInstallments = input.totalInstallments();

        // Divide o valor total em parcelas iguais.
        // Usa escala 2 e HALF_EVEN para evitar distorções financeiras
        BigDecimal installmentValue = totalValue.divide(BigDecimal.valueOf(totalInstallments), 2, RoundingMode.HALF_EVEN);
        BigDecimal remainder = totalValue.subtract(installmentValue.multiply(BigDecimal.valueOf(totalInstallments)));
        
        UUID transactionId = null;
        // Cria e salva as parcelas
        for (int i = 1; i <= totalInstallments; i++){
            var currentInstallmentsValue = (i ==1) ? installmentValue.add(remainder) : installmentValue;

            // Cada parcela pertence a uma fatura diferente:
            // - parcela 1 → fatura base
            // - parcela 2 → mês seguinte
            // - parcela N → base + (N - 1)
            YearMonth invoiceMonth = baseInvoiceMonth.plusMonths(i - 1);

            // Reutiliza a fatura aberta do mês ou cria uma nova se não existir.
            // Garante que exista no máximo uma fatura aberta por mêsr
            Invoice invoice = creditCard.findOpenInvoice(invoiceMonth);

            CardTransaction installmentTransaction = new CardTransaction(
                    creditCard.getBankAccountId(),
                    creditCard,
                    category,
                    invoice,
                    currentInstallmentsValue,
                    i, // numero da parcela
                    totalInstallments,
                    input.description()
            );
            
            invoice.addTransaction(installmentTransaction);
            category.addTransaction(installmentTransaction);

            transactionId = installmentTransaction.getId();
        }
        return transactionId;
    }
}

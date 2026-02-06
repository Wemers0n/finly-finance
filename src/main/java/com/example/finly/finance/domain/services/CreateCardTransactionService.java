package com.example.finly.finance.domain.services;

import com.example.finly.finance.application.dtos.in.CardTransactionInput;
import com.example.finly.finance.domain.model.*;
import com.example.finly.finance.domain.repository.UserRepository;
import com.example.finly.finance.infraestructure.handler.exception.CategoryNotFoundException;
import com.example.finly.finance.infraestructure.handler.exception.CreditCardNotFoundException;
import com.example.finly.finance.infraestructure.handler.exception.UserNotExistsException;
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

    private final UserRepository userRepository;

    public UUID cardTransaction(UUID userId, CardTransactionInput input){

        User user = this.findUser(userId);
        CreditCard creditCard = this.findCreditCard(user, input.cardId());
        Category category = this.findCategory(user, input.categoryName());

        // Autoriza no domínio antes de qualquer persistência
        // Garante que não será criada transação sem limite disponível
        creditCard.authorize(input.value());

        this.createInstallments(creditCard, category, input);

        // persistencia via cascade
        this.userRepository.save(user);

        return creditCard.getId();
    }

    private User findUser(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(UserNotExistsException::new);
    }

    private CreditCard findCreditCard(User user, UUID cardId){
        return user.findCardById(cardId)
                .orElseThrow(CreditCardNotFoundException::new);
    }

    private Category findCategory(User user, String categoryName){
        return user.findCategoryByName(categoryName)
                .orElseThrow(CategoryNotFoundException::new);
    }

    private void createInstallments(CreditCard creditCard, Category category, CardTransactionInput input){

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
        }
    }
}

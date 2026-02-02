package com.example.finly.finance.domain.services;

import com.example.finly.finance.application.dtos.in.CardTransactionInput;
import com.example.finly.finance.domain.model.*;
import com.example.finly.finance.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));
    }

    private CreditCard findCreditCard(User user, UUID cardId){
        return user.findCardById(cardId)
                .orElseThrow(() -> new RuntimeException("Error: card does not exist"));
    }

    private Category findCategory(User user, String categoryName){
        return user.findCategoryByName(categoryName)
                .orElseThrow(() -> new RuntimeException("Error: category does not exist"));
    }

    private void createInstallments(CreditCard creditCard, Category category, CardTransactionInput input){
        BigDecimal totalValue = input.value();
        Integer totalInstallments = input.totalInstallments();

        // Divide o valor total em parcelas iguais,
        // aplicando o resto da divisão apenas na primeira parcela
        // para evitar perda de centavos
        BigDecimal installmentValue = totalValue.divide(BigDecimal.valueOf(totalInstallments), 2, RoundingMode.HALF_EVEN);
        BigDecimal remainder = totalValue.subtract(installmentValue.multiply(BigDecimal.valueOf(totalInstallments)));

        var currentMonth = YearMonth.now();

        // Cria e salva as parcelas
        for (int i = 1; i <= totalInstallments; i++){
            var currentInstallmentsValue = (i ==1) ? installmentValue.add(remainder) : installmentValue;

            // Cada parcela pertence à fatura do mês correspondente
            var referenceMonth = currentMonth.plusMonths(i - 1);

            // Reutilizar a fatura aberta do mês ou cria uma nova se não existir
            Invoice invoice = creditCard.findOpenInvoice(referenceMonth)
                    .orElseGet(() -> creditCard.createInvoice(referenceMonth));

            CardTransaction installmentTransaction = new CardTransaction(
                    creditCard,
                    category,
                    invoice,
                    currentInstallmentsValue,
                    i,
                    totalInstallments,
                    input.description()
            );

            invoice.addTransaction(installmentTransaction);
            category.addTransaction(installmentTransaction);
        }
    }
}

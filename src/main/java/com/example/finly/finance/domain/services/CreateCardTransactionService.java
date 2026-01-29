package com.example.finly.finance.domain.services;

import com.example.finly.finance.application.dtos.CardTransactionInput;
import com.example.finly.finance.domain.model.CardTransaction;
import com.example.finly.finance.domain.model.Category;
import com.example.finly.finance.domain.model.CreditCard;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.repository.TransactionRepository;
import com.example.finly.finance.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateCardTransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public UUID cardTransaction(UUID userId, CardTransactionInput input){

        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));

        CreditCard creditCard = user.findCardById(input.cardId())
                .orElseThrow(() -> new RuntimeException("Error: card does not exist"));

        Category category = user.findCategoryByName(input.categoryName())
                .orElseThrow(() -> new RuntimeException("Error: category does not exist"));

        // Autorizar no dominio antes de fazer a persistencia
        creditCard.authorize(input.value());

        // CardTransaction transaction = new CardTransaction(creditCard, category, input.value(), input.installNumber(), input.totalInstallments(), input.description());

        BigDecimal totalValue = input.value();
        Integer totalInstallments = input.totalInstallments();

        // Calculo da parcela e arredondamento do valor
        BigDecimal installmentValue = totalValue.divide(BigDecimal.valueOf(totalInstallments), 2, RoundingMode.HALF_EVEN);
        BigDecimal remainder = totalValue.subtract(installmentValue.multiply(BigDecimal.valueOf(totalInstallments)));

        // Criar e salvar as parcelas
        for (int i = 1; i <= totalInstallments; i++){
            var currentInstallmentsValue = (i ==1) ? installmentValue.add(remainder) : installmentValue;

            CardTransaction installmentTransaction = new CardTransaction(
                    creditCard,
                    category,
                    currentInstallmentsValue,
                    i,
                    totalInstallments,
                    input.description()
            );

            category.addTransaction(installmentTransaction);
            this.transactionRepository.save(installmentTransaction);
        }
        return creditCard.getId();
    }
}

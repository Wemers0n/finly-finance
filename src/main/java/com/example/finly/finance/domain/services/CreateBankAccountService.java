package com.example.finly.finance.domain.services;

import com.example.finly.finance.application.dtos.in.BankAccountInput;
import com.example.finly.finance.domain.model.User;
import com.example.finly.finance.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateBankAccountService {

    private final UserRepository userRepository;

    public UUID create(UUID userId, BankAccountInput input){

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        UUID bankId = user.addBankAccount(
                input.accountName(),
                input.accountType(),
                input.initialBalance()
        );

        this.userRepository.save(user);
        return bankId;
    }
}

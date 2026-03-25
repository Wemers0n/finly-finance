package com.example.finly.finance.domain.services.account;

import com.example.finly.finance.application.dtos.out.BankAccountOutput;
import com.example.finly.finance.domain.repository.UserRepository;
import com.example.finly.finance.infraestructure.handler.exception.UserNotExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserBankAccountsService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<BankAccountOutput> listAccounts(UUID userId) {
        var user = userRepository.findByIdWithBankAccounts(userId)
                .orElseThrow(UserNotExistsException::new);

        return user.findAllAccounts()
                .stream()
                .map(account -> new BankAccountOutput(
                        account.getId(),
                        account.getAccountName(),
                        account.getAccountType(),
                        account.getCurrentBalance()
                ))
                .toList();
    }
}

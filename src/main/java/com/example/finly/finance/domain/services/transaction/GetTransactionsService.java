package com.example.finly.finance.domain.services.transaction;

import com.example.finly.finance.application.dtos.out.TransactionOutput;
import com.example.finly.finance.application.mapper.TransactionMapper;
import com.example.finly.finance.domain.model.Transaction;
import com.example.finly.finance.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetTransactionsService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public List<TransactionOutput> listByAccount(UUID accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        return transactionMapper.toDtoList(transactions);
    }
}

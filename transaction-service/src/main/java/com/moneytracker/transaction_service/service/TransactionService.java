package com.moneytracker.transaction_service.service;

import com.moneytracker.transaction_service.dto.TransactionRequest;
import com.moneytracker.transaction_service.dto.TransactionResponse;
import com.moneytracker.transaction_service.entity.TransactionEntity;
import com.moneytracker.transaction_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionResponse create(TransactionRequest transactionRequest) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setAmount(transactionRequest.amount());
        transactionEntity.setDescription(transactionRequest.description());
        transactionEntity.setType(transactionRequest.type());
        transactionEntity.setData(transactionRequest.data());
        transactionEntity.setCategory(transactionRequest.category());
        TransactionEntity saved = transactionRepository.save(transactionEntity);

        log.info("Transaction created with ID: {}", saved.getId());

        return TransactionResponse.fromEntity(saved);
    }
}

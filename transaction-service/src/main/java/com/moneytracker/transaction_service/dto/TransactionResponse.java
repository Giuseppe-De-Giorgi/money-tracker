package com.moneytracker.transaction_service.dto;

import com.moneytracker.transaction_service.entity.TransactionEntity;
import com.moneytracker.transaction_service.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        String description,
        TransactionType type,
        LocalDate data,
        String category
) {
    public static TransactionResponse fromEntity(TransactionEntity entity) {
        return new TransactionResponse(
                entity.getId(),
                entity.getAmount(),
                entity.getDescription(),
                entity.getType(),
                entity.getData(),
                entity.getCategory()
        );
    }
}

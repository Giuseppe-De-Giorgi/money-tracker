package com.moneytracker.transaction_service.dto;

import com.moneytracker.transaction_service.enums.TransactionType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
        @NotNull @Positive @Digits(integer = 17, fraction = 2)
        BigDecimal amount,
        @Size(max = 255)
        String description,
        @NotNull
        TransactionType type,
        @NotNull
        LocalDate data,
        @NotNull @Size(max = 50)
        String category
) {
}

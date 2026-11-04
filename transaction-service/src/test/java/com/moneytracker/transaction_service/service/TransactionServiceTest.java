package com.moneytracker.transaction_service.service;

import com.moneytracker.transaction_service.dto.TransactionRequest;
import com.moneytracker.transaction_service.dto.TransactionResponse;
import com.moneytracker.transaction_service.entity.TransactionEntity;
import com.moneytracker.transaction_service.enums.TransactionType;
import com.moneytracker.transaction_service.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void create_savesAndReturnsResponse() {
        TransactionRequest request = new TransactionRequest(
                new BigDecimal("25.50"),
                "Spesa",
                TransactionType.EXPENSE,
                LocalDate.of(2026, 11, 3),
                "Food"
        );

        TransactionEntity saved = new TransactionEntity();
        saved.setId(10L);
        saved.setAmount(request.amount());
        saved.setDescription(request.description());
        saved.setType(request.type());
        saved.setData(request.data());
        saved.setCategory(request.category());
        when(transactionRepository.save(any())).thenReturn(saved);

        TransactionResponse response = transactionService.create(request);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.amount()).isEqualByComparingTo(request.amount());
        assertThat(response.description()).isEqualTo(request.description());
        assertThat(response.type()).isEqualTo(request.type());
        assertThat(response.data()).isEqualTo(request.data());
        assertThat(response.category()).isEqualTo(request.category());

        ArgumentCaptor<TransactionEntity> captor = ArgumentCaptor.forClass(TransactionEntity.class);
        verify(transactionRepository).save(captor.capture());
        TransactionEntity passed = captor.getValue();
        assertThat(passed.getAmount()).isEqualByComparingTo(request.amount());
        assertThat(passed.getCategory()).isEqualTo(request.category());
        assertThat(passed.getType()).isEqualTo(request.type());
    }
}

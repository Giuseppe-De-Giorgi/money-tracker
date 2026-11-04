package com.moneytracker.transaction_service.service;

import com.moneytracker.transaction_service.dto.TransactionRequest;
import com.moneytracker.transaction_service.dto.TransactionResponse;
import com.moneytracker.transaction_service.entity.TransactionEntity;
import com.moneytracker.transaction_service.enums.TransactionType;
import com.moneytracker.transaction_service.exception.TransactionNotFoundException;
import com.moneytracker.transaction_service.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
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

    @Test
    void getById_existingId_returnsResponse() {
        TransactionEntity entity = new TransactionEntity();
        entity.setId(1L);
        entity.setAmount(new BigDecimal("25.50"));
        entity.setDescription("Spesa");
        entity.setType(TransactionType.EXPENSE);
        entity.setData(LocalDate.of(2026, 11, 3));
        entity.setCategory("Food");
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(entity));

        TransactionResponse response = transactionService.getById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.amount()).isEqualByComparingTo(new BigDecimal("25.50"));
        assertThat(response.description()).isEqualTo("Spesa");
        assertThat(response.type()).isEqualTo(TransactionType.EXPENSE);
        assertThat(response.data()).isEqualTo(LocalDate.of(2026, 11, 3));
        assertThat(response.category()).isEqualTo("Food");
        verify(transactionRepository, times(1)).findById(1L);
    }

    @Test
    void getById_nonExistingId_throwsNotFound() {
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        TransactionNotFoundException ex = assertThrows(TransactionNotFoundException.class,
                () -> transactionService.getById(999L));

        assertEquals("Transaction not found with id: 999", ex.getMessage());
        verify(transactionRepository).findById(999L);
    }
}

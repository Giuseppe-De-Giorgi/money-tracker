package com.moneytracker.transaction_service.controller;

import com.moneytracker.transaction_service.dto.TransactionResponse;
import com.moneytracker.transaction_service.enums.TransactionType;
import com.moneytracker.transaction_service.exception.TransactionNotFoundException;
import com.moneytracker.transaction_service.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    void createTransaction_validRequest_returns201() throws Exception {
        TransactionResponse response = new TransactionResponse(
                1L,
                new BigDecimal("50.00"),
                "Groceries",
                TransactionType.EXPENSE,
                LocalDate.of(2026, 11, 2),
                "Food"
        );
        when(transactionService.create(any())).thenReturn(response);

        String body = """
                {
                  "amount": 50.00,
                  "description": "Groceries",
                  "type": "EXPENSE",
                  "data": "2026-11-02",
                  "category": "Food"
                }
                """;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/transactions/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(50.00))
                .andExpect(jsonPath("$.type").value("EXPENSE"))
                .andExpect(jsonPath("$.data").value("2026-11-02"))
                .andExpect(jsonPath("$.category").value("Food"))
                ;
        verify(transactionService, times(1)).create(any());
    }

    @Test
    void createTransaction_invalidRequest_returns400() throws Exception {
        String body = """
                {
                  "amount": -5.12,
                  "description": "Groceries",
                  "type": "EXPENSE",
                  "data": "2026-11-02",
                  "category": ""
                }
                """;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.amount").exists())
                .andExpect(jsonPath("$.errors.category").exists());

        verifyNoInteractions(transactionService);
    }

    @Test
    void createTransaction_missingType_returns400() throws Exception {
        String body = """
                {
                  "amount": 50.00,
                  "description": "Groceries",
                  "data": "2026-11-02",
                  "category": "Food"
                }
                """;

        mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.type").exists());

        verify(transactionService, times(0)).create(any());
    }

    @Test
    void getTransaction_return200() throws Exception{
        TransactionResponse response = new TransactionResponse(
                1L,
                new BigDecimal("50.00"),
                "Groceries",
                TransactionType.EXPENSE,
                LocalDate.of(2026, 11, 2),
                "Food"
        );
        when(transactionService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/transactions/{id}", 1L))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.amount").value(50.00))
                .andExpect(jsonPath("$.type").value("EXPENSE"))
                .andExpect(jsonPath("$.data").value("2026-11-02"))
                .andExpect(jsonPath("$.category").value("Food"))
                .andExpect(status().isOk());
    }

    @Test
    void getTransaction_return404() throws Exception{
        when(transactionService.getById(999L)).thenThrow(new TransactionNotFoundException(999L));
        mockMvc.perform(get("/transactions/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Transaction not found with id: 999"));
    }
}

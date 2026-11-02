package com.moneytracker.transaction_service.controller;

import com.moneytracker.transaction_service.dto.TransactionResponse;
import com.moneytracker.transaction_service.enums.TransactionType;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
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
                .andExpect(jsonPath("$.category").value("Food"));
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
}

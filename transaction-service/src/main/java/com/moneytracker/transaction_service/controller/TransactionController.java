package com.moneytracker.transaction_service.controller;

import com.moneytracker.transaction_service.dto.TransactionRequest;
import com.moneytracker.transaction_service.dto.TransactionResponse;
import com.moneytracker.transaction_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<@NonNull TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest transactionRequest) {
        TransactionResponse response = transactionService.create(transactionRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NonNull TransactionResponse> getTransactionById(@PathVariable Long id){
        TransactionResponse response = transactionService.getById(id);
        return ResponseEntity.ok(response);
    }
}

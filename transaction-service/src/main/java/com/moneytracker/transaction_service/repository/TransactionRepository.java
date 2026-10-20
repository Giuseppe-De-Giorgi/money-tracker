package com.moneytracker.transaction_service.repository;

import com.moneytracker.transaction_service.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
}

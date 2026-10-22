package com.moneytracker.transaction_service.repository;

import com.moneytracker.transaction_service.entity.TransactionEntity;
import com.moneytracker.transaction_service.enums.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class TransactionRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void save_and_find_by_id() {
        // given
        TransactionEntity transaction = new TransactionEntity();
        transaction.setAmount(new BigDecimal("12.50"));
        transaction.setType(TransactionType.EXPENSE);
        transaction.setCategory("Food");
        transaction.setData(LocalDate.of(2026, 10, 21));
        transaction.setDescription("Lunch");

        // when
        TransactionEntity saved = transactionRepository.save(transaction);
        Optional<TransactionEntity> found = transactionRepository.findById(saved.getId());

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(found).isPresent();
        TransactionEntity result = found.get();
        assertThat(result.getAmount()).isEqualByComparingTo("12.50");
        assertThat(result.getType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(result.getCategory()).isEqualTo("Food");
        assertThat(result.getData()).isEqualTo(LocalDate.of(2026, 10, 21));
        assertThat(result.getDescription()).isEqualTo("Lunch");
    }

    @Test
    void save_without_category_fails() {
        // given
        TransactionEntity transaction = new TransactionEntity();
        transaction.setAmount(new BigDecimal("12.50"));
        transaction.setType(TransactionType.EXPENSE);
        transaction.setData(LocalDate.of(2026, 10, 21));
        transaction.setDescription("Lunch");

        // when / then
        assertThatThrownBy(() -> transactionRepository.saveAndFlush(transaction))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}

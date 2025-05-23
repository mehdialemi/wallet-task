package org.example.wallet.repository;

import org.example.wallet.model.ScheduledTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledTransactionRepository extends JpaRepository<ScheduledTransaction, Long> {
    List<ScheduledTransaction> findByExecutedFalseAndScheduledTimeBefore(LocalDateTime time);
}

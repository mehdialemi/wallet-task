package org.example.wallet.repository;

import org.example.wallet.model.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByOwner(String owner);
    Page<WalletTransaction> findByOwner(String owner, Pageable pageable);

    Page<WalletTransaction> findByOwnerAndTimestampBetween(
            String owner, LocalDateTime from, LocalDateTime to, Pageable pageable);
}

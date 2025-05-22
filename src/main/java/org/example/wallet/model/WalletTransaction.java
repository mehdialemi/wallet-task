package org.example.wallet.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String owner;
    private String type; // ADD, WITHDRAW, TRANSFER
    private BigDecimal amount;
    private String note;
    private LocalDateTime timestamp = LocalDateTime.now();

    public WalletTransaction(String owner, String type, BigDecimal amount, String note) {
        this.owner = owner;
        this.type = type;
        this.amount = amount;
        this.note = note;
    }
}

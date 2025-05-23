package org.example.wallet.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(indexes = {@Index(columnList = "owner,type,timestamp")})
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

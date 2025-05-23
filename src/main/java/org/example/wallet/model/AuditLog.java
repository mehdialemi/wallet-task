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
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String owner;
    private String action;
    private BigDecimal amount;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();

    public AuditLog(String owner, String action, BigDecimal amount, String message) {
        this.owner = owner;
        this.action = action;
        this.amount = amount;
        this.message = message;
    }

}

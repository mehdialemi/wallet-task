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
public class ScheduledTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String owner;
    private String targetOwner;
    private BigDecimal amount;
    private String type;
    private LocalDateTime scheduledTime;
    private boolean executed = false;

    public ScheduledTransaction(String owner, String targetOwner, BigDecimal amount, String type, LocalDateTime scheduledTime) {
        this.owner = owner;
        this.targetOwner = targetOwner;
        this.amount = amount;
        this.type = type;
        this.scheduledTime = scheduledTime;
    }
}

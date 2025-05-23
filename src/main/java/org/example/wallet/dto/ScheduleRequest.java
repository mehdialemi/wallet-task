package org.example.wallet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ScheduleRequest {
    private String targetOwner;
    private BigDecimal amount;
    private String type;
    private String scheduledTime;
}

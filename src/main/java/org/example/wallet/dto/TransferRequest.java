package org.example.wallet.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    private String toUser;
    private BigDecimal amount;
}

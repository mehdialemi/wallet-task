package org.example.wallet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddFundRequest {
    private BigDecimal amount;
}

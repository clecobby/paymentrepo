package com.example.paymentapi.model;

import java.math.BigDecimal;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WalletDebitRequestGeneral {
   
    private String walletNumber;

    private String provider;
    @NotNull
    private BigDecimal amount;
    
    @NotNull
    private String retailorId;
    private String narration;

    @Size(max = 80)
    private String reference;
    private String customerRemarks;

    private String customerName;
    private String aggregator;
    private Map<String, Object> extra; 
}

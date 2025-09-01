package com.example.paymentapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class WalletDebitRequest {

    private String walletNumber;
    @NotNull
    private String retailorId;
    private String walletProvider;
    @NotNull
    private BigDecimal amount;

    private String narration;

    @Size(max = 80)
    private String reference;
    private String customerRemarks;

    private String customerName;
    private Map<String, Object> extra;
}
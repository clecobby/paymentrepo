package com.example.paymentapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Data
public class WalletKYCRequest {

    private String walletNumber;
    private String aggregator;
    private String provider;

    @Size(max = 80)
    private String reference;
    private Map<String, Object> extra;
    private String retailorId;
}
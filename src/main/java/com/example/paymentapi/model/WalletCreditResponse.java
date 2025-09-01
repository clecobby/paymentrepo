package com.example.paymentapi.model;

import lombok.Data;

import java.util.Map;

@Data
public class WalletCreditResponse {
    private int status;
    private String message;
    private Map<String, Object> data;
}
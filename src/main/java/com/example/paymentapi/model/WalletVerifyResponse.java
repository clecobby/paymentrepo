package com.example.paymentapi.model;

import lombok.Data;

import java.util.Map;

@Data
public class WalletVerifyResponse {
    private int status;
    private String message;
    private CustomerData data;
    private String timeStamp;
}
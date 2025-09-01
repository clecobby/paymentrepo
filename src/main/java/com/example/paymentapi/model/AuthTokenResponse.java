package com.example.paymentapi.model;

import lombok.Data;

import java.util.Map;

@Data
public class AuthTokenResponse {
    private int status;
    private String message;
    private Map<String, String> data; // token, expiry, merchantName
}
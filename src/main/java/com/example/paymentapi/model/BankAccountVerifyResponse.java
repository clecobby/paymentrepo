package com.example.paymentapi.model;

import lombok.Data;

import java.util.Map;

@Data
public class BankAccountVerifyResponse {
    private int status;
    private String message;
    private Map<String, String> data; // name, timeStamp, extra
}
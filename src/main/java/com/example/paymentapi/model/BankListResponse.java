package com.example.paymentapi.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BankListResponse {
    private int status;
    private String message;
    private List<Map<String, String>> data; // bankName, shortName, etc.
}
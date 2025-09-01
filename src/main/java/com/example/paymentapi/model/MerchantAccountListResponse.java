package com.example.paymentapi.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MerchantAccountListResponse {
    private int status;
    private String message;
    private List<Map<String, Object>> data; // accountNumber, customerName, etc.
}
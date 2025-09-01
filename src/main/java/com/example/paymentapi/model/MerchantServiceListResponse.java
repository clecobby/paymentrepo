package com.example.paymentapi.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MerchantServiceListResponse {
    private int status;
    private String message;
    private List<Map<String, Object>> data; // serviceId, serviceName, etc.
}
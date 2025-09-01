package com.example.paymentapi.model;

import lombok.Data;

import java.util.Map;

@Data
public class StatusQueryRequest {
    private String transactionId;
    private String reference;
    private String aggregator;
    private Map<String, Object> extra;
}
package com.example.paymentapi.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class StatusQueryResponse {
    private int status;
    private String message;
    private String transactionId;
    private String reference;
    private String beneficiary;
    private String transType;
    private BigDecimal amount;
    private String requestDate;
    private String approveDate;
    private String narration;
    private String timeStamp;
}
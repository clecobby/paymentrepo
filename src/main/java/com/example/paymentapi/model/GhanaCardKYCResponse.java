package com.example.paymentapi.model;

import lombok.Data;

import java.util.Map;

@Data
public class GhanaCardKYCResponse {
    private int status;
    private String message;
    private Map<String, String> data; // nationalId, cardId, etc., extra
}
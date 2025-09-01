package com.example.paymentapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Data
public class BankAccountVerifyRequest {

    private String accountNumber;

    private String sortCode;

    @Size(max = 80)
    private String reference;
    private Map<String, Object> extra;
}
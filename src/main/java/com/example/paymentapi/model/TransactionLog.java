package com.example.paymentapi.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;


@Data
@Entity
@Table(name = "transaction_logs")
public class TransactionLog {
    @Id
    @Column(nullable = false, unique = true)
    private String reference;

    @Column
    private String provider;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column
    private String beneficiary;

    @Column
    private String aggregator;

    @Column
    private String transType;

    @Column
    private String narration;

    @Column
    private String message;

    @Column
    private String status;

    @Column
    private String requestDate;

    @Column
    private String approveDate;
    private String retailorId;
    @Column(nullable = false)
    private String timestamp;

}

package com.example.paymentapi.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "account_details")
public class AccountRequestLog {
    @Id
    @Column(nullable = false, unique = true)
    private String retailorId;
    private String username;
    private String email;
    private String status;
    private BigDecimal walletBalance;
    private BigDecimal totalBalance;
    private LocalDateTime createdAt;

}

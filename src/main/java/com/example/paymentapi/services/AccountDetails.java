// package com.example.paymentapi.services;

// import com.example.paymentapi.model.AccountCreditRequest;
// import com.example.paymentapi.model.TransactionLog;
// import com.example.paymentapi.model.WalletCreditRequest;
// import com.example.paymentapi.model.WalletDebitRequest;
// import com.example.paymentapi.model.WalletDebitRequestGeneral;
// import com.example.paymentapi.model.WalletDebitResponseGeneral;
// import com.example.paymentapi.repository.AccountDetailsRespository;
// import com.example.paymentapi.repository.TransactionLogRepository;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;

// @Service
// public class AccountDetails {

//     private static final Logger logger = LoggerFactory.getLogger(TransactionLogService.class);

//     private final AccountDetailsRespository repository;
//     private final ObjectMapper objectMapper;

//     @Autowired
//     public AccountDetails(AccountDetails repository, ObjectMapper objectMapper) {
//         // this.repository = repository;
//         this.objectMapper = objectMapper;
//     }

//     // check duplicates
//     public boolean referenceExists(String reference) {
//         return repository.existsByReference(reference);
//     }

//     // Create initial pending transaction
//     public TransactionLog logTransaction(WalletDebitRequestGeneral request) {
//         TransactionLog log = new TransactionLog();
//         log.setReference(request.getReference());
//         log.setProvider(request.getProvider());
//         log.setAmount(request.getAmount());
//         log.setBeneficiary(request.getWalletNumber());
//         log.setNarration(request.getNarration());
//         log.setStatus("PENDING");
//         log.setTimestamp(LocalDateTime.now().toString());
//         log.setAggregator(request.getAggregator());

//         validateLog(log, true);
//         return repository.save(log);
//     }

  
//     // Update log after provider response
//     public void updateLogStatus(WalletDebitResponseGeneral response) {
//         TransactionLog log = repository.findByReference(response.getReference())
//                 .orElseThrow(
//                         () -> new IllegalArgumentException(
//                                 "Transaction log not found for reference " + response.getReference()));

//         log.setStatus(String.valueOf(response.getStatus()));
//         log.setMessage(response.getMessage());
//         log.setReference(response.getReference());
//         log.setApproveDate(response.getApproveDate());
//         log.setStatus(String.valueOf(response.getStatus()));
//         log.setApproveDate(response.getApproveDate());
//         log.setBeneficiary(response.getBeneficiary());
//         log.setRequestDate(response.getRequestDate());
//         log.setApproveDate(response.getApproveDate());
//         log.setTimestamp(response.getTimeStamp());
//         log.setTransType(response.getTransType());

//         repository.save(log);
//     }

//     private void validateLog(TransactionLog log, boolean requireWalletProvider) {
//         if (log.getAmount() == null) {
//             throw new IllegalArgumentException("Amount cannot be null");
//         }
//         if (log.getReference() == null) {
//             throw new IllegalArgumentException("Reference cannot be null");
//         }
//         if (requireWalletProvider && log.getProvider() == null) {
//             throw new IllegalArgumentException("Wallet provider cannot be null");
//         }

//     }
// }
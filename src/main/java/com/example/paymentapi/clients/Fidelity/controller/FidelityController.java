package com.example.paymentapi.clients.Fidelity.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import com.example.paymentapi.clients.Fidelity.service.FidelityService;
import com.example.paymentapi.clients.Fidelity.service.TokenManager;
import com.example.paymentapi.model.AccountCreditRequest;
import com.example.paymentapi.model.AccountCreditRequestGeneral;
import com.example.paymentapi.model.AccountCreditResponse;
import com.example.paymentapi.model.AccountCreditResponseGeneral;
import com.example.paymentapi.model.StatusQueryRequest;
import com.example.paymentapi.model.StatusQueryResponse;
import com.example.paymentapi.model.WalletDebitRequest;
import com.example.paymentapi.model.WalletDebitRequestGeneral;
import com.example.paymentapi.model.WalletDebitResponse;
import com.example.paymentapi.model.WalletDebitResponseGeneral;
import com.example.paymentapi.model.WalletKYCRequest;
import com.example.paymentapi.model.WalletKYCResponse;
import com.example.paymentapi.model.WalletVerifyRequest;
import com.example.paymentapi.model.WalletVerifyResponse;
import com.example.paymentapi.services.TransactionLogService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@Service
public class FidelityController {
  private static final Logger logger = LoggerFactory.getLogger(FidelityController.class);
  @Autowired
  private TransactionLogService transactionLogService;
  @Autowired
  private FidelityService fidelityService;
  private WalletDebitResponseGeneral response = new WalletDebitResponseGeneral();
  private WalletDebitResponse fidelityResponse = new WalletDebitResponse();

  public WalletDebitResponseGeneral debitCustomerWallet(WalletDebitRequestGeneral request) throws Exception {
    logger.info("Inside Fidelity Controller for debiting customer ");

    // if exists
    if (request.getReference() != null && transactionLogService.referenceExists(request.getReference())) {
      logger.warn("Duplicate transaction reference detected: {}", request.getReference());
      response.setStatus(888);
      response.setMessage("Duplicate transaction reference: " + request.getReference());
      response.setReference(request.getReference());
      response.setTimeStamp(LocalDateTime.now().toString());
      response.setAmount(request.getAmount());

      return response;
    }
    // set fidelity request
    WalletDebitRequest newRequest = new WalletDebitRequest();
    newRequest.setAmount(request.getAmount());
    newRequest.setCustomerName(request.getCustomerName());
    newRequest.setCustomerRemarks(request.getCustomerRemarks());
    newRequest.setNarration(request.getNarration());
    newRequest.setReference(request.getReference());
    newRequest.setWalletNumber(request.getWalletNumber());
    newRequest.setWalletProvider(request.getProvider());
    newRequest.setExtra(request.getExtra());

    try {
      transactionLogService.logTransaction(request);

      fidelityResponse = fidelityService.debitWallet(newRequest);

      response.setStatus(fidelityResponse.getStatus());
      response.setMessage(fidelityResponse.getMessage());
      response.setTimeStamp(fidelityResponse.getTimeStamp());
      response.setReference(request.getReference());
      response.setBeneficiary(fidelityResponse.getBeneficiary());
      response.setTransType(fidelityResponse.getTransType());
      response.setAmount(fidelityResponse.getAmount());
      response.setRequestDate(fidelityResponse.getRequestDate());
      response.setApproveDate(fidelityResponse.getApproveDate());
      response.setNarration(fidelityResponse.getNarration());

      // update transaction
      logger.info("updating wallet request with immediate response {}", response);
      transactionLogService.updateLogStatus(response);

    } catch (Exception e) {
      response.setStatus(-1);
      response.setMessage("Transaction failed: " + e.getMessage());
      response.setReference(request.getReference());
      response.setTimeStamp(LocalDateTime.now().toString());

      transactionLogService.updateLogStatus(response);

      // Log error
      logger.info("Error during debitCustomerWallet for reference {}: {}", request.getReference(), e.getMessage(), e);

    }

    return response;
  }

  public AccountCreditResponseGeneral creditAccount(AccountCreditRequestGeneral request) throws Exception {
    logger.info("Inside Fidelity Controller for crediting customer ");
    AccountCreditResponseGeneral response = new AccountCreditResponseGeneral();
    AccountCreditResponse fidelityResponse = new AccountCreditResponse();
    // if exists
    if (request.getReference() != null && transactionLogService.referenceExists(request.getReference())) {
      logger.warn("Duplicate transaction reference detected: {}", request.getReference());
      response.setStatus(888);
      response.setMessage("Duplicate transaction reference: " + request.getReference());
      response.setReference(request.getReference());
      response.setTimeStamp(LocalDateTime.now().toString());
      response.setAmount(request.getAmount());

      return response;
    }
    // set fidelity request
    AccountCreditRequest newRequest = new AccountCreditRequest();
    newRequest.setAmount(request.getAmount());
    newRequest.setCustomerName(request.getCustomerName());
    newRequest.setCustomerRemarks(request.getCustomerRemarks());
    newRequest.setNarration(request.getNarration());
    newRequest.setReference(request.getReference());
    newRequest.setAccountNumber(request.getAccountNumber());
    newRequest.setSortCode(request.getSortCode());
    newRequest.setExtra(request.getExtra());

    try {
      transactionLogService.logTransactionAccount(request);

      fidelityResponse = fidelityService.creditAccount(newRequest);

      response.setStatus(fidelityResponse.getStatus());
      response.setMessage(fidelityResponse.getMessage());
      response.setTimeStamp(fidelityResponse.getTimeStamp());
      response.setReference(request.getReference());
      response.setBeneficiary(fidelityResponse.getBeneficiary());
      response.setTransType(fidelityResponse.getTransType());
      response.setAmount(fidelityResponse.getAmount());
      response.setRequestDate(fidelityResponse.getRequestDate());
      response.setApproveDate(fidelityResponse.getApproveDate());
      response.setNarration(fidelityResponse.getNarration());

      // update transaction
      logger.info("updating wallet request with immediate response {}", response);
      transactionLogService.updateLogStatusAccount(response);

    } catch (Exception e) {
      response.setStatus(-1);
      response.setMessage("Transaction failed: " + e.getMessage());
      response.setReference(request.getReference());
      response.setTimeStamp(LocalDateTime.now().toString());

      transactionLogService.updateLogStatusAccount(response);

      // Log error
      logger.info("Error during creditCustomerWallet for reference {}: {}", request.getReference(), e.getMessage(), e);

    }

    return response;
  }

  public WalletDebitResponseGeneral creditCustomerWallet(WalletDebitRequestGeneral request) throws Exception {
    logger.info("Inside Fidelity Controller for crediting customer ");

    // if exists
    if (request.getReference() != null && transactionLogService.referenceExists(request.getReference())) {
      logger.warn("Duplicate transaction reference detected: {}", request.getReference());
      response.setStatus(888);
      response.setMessage("Duplicate transaction reference: " + request.getReference());
      response.setReference(request.getReference());
      response.setTimeStamp(LocalDateTime.now().toString());
      response.setAmount(request.getAmount());

      return response;
    }
    // set fidelity request
    WalletDebitRequest newRequest = new WalletDebitRequest();
    newRequest.setAmount(request.getAmount());
    newRequest.setCustomerName(request.getCustomerName());
    newRequest.setCustomerRemarks(request.getCustomerRemarks());
    newRequest.setNarration(request.getNarration());
    newRequest.setReference(request.getReference());
    newRequest.setWalletNumber(request.getWalletNumber());
    newRequest.setWalletProvider(request.getProvider());
    newRequest.setExtra(request.getExtra());

    try {
      transactionLogService.logTransaction(request);

      fidelityResponse = fidelityService.debitWallet(newRequest);

      response.setStatus(fidelityResponse.getStatus());
      response.setMessage(fidelityResponse.getMessage());
      response.setTimeStamp(fidelityResponse.getTimeStamp());
      response.setReference(request.getReference());
      response.setBeneficiary(fidelityResponse.getBeneficiary());
      response.setTransType(fidelityResponse.getTransType());
      response.setAmount(fidelityResponse.getAmount());
      response.setRequestDate(fidelityResponse.getRequestDate());
      response.setApproveDate(fidelityResponse.getApproveDate());
      response.setNarration(fidelityResponse.getNarration());

      // update transaction
      logger.info("updating wallet request with immediate response {}", response);
      transactionLogService.updateLogStatus(response);

    } catch (Exception e) {
      response.setStatus(-1);
      response.setMessage("Transaction failed: " + e.getMessage());
      response.setReference(request.getReference());
      response.setTimeStamp(LocalDateTime.now().toString());

      transactionLogService.updateLogStatus(response);

      // Log error
      logger.info("Error during creditCustomerWallet for reference {}: {}", request.getReference(), e.getMessage(), e);

    }

    return response;
  }

  public StatusQueryResponse checkStatus(StatusQueryRequest request) {
    StatusQueryResponse statusQueryResponse = new StatusQueryResponse();

    logger.info("Inside Fidelity Controller for Status Checks {}", request);

    try {
      statusQueryResponse = fidelityService.checkStatus(request);
      logger.info("status Query Response **** {}", statusQueryResponse.toString());
      if (statusQueryResponse.getStatus() != 3) {
        logger.info("Updating record with new STATUS ***** " + statusQueryResponse.getMessage());

        response.setReference(request.getReference());
        response.setStatus(statusQueryResponse.getStatus());
        response.setMessage(statusQueryResponse.getMessage());
        response.setTimeStamp(statusQueryResponse.getTimeStamp());
        response.setReference(request.getReference());
        response.setBeneficiary(statusQueryResponse.getBeneficiary());
        response.setTransType(statusQueryResponse.getTransType());
        response.setAmount(statusQueryResponse.getAmount());
        response.setRequestDate(statusQueryResponse.getRequestDate());
        response.setApproveDate(statusQueryResponse.getApproveDate());
        response.setNarration(statusQueryResponse.getNarration());

        transactionLogService.updateLogStatus(response);

      } else {
        statusQueryResponse.setReference(request.getReference());
      }

    } catch (Exception e) {
      logger.error("Error while checking status", e);
    }
    return statusQueryResponse;
  }

  public WalletKYCResponse verifyWallet(@Valid @RequestBody WalletKYCRequest request) throws Exception {

    WalletKYCResponse response = new WalletKYCResponse();
    WalletVerifyResponse walletVerifyResponse = new WalletVerifyResponse();

    if (request.getAggregator().equals("FIDELITY")) {

      logger.info("Received wallet verification request: {}", request);

      WalletVerifyRequest walletVerifyRequest = new WalletVerifyRequest();
      walletVerifyRequest.setWalletProvider(request.getProvider());
      walletVerifyRequest.setWalletNumber(request.getWalletNumber());
      walletVerifyRequest.setReference(request.getReference());

      walletVerifyResponse = fidelityService.verifyWallet(walletVerifyRequest);

      logger.info("walletVerifyResponse  {}", walletVerifyResponse);
      response.setMessage(walletVerifyResponse.getMessage());
      response.setData(walletVerifyResponse.getData());
      response.setStatus(walletVerifyResponse.getStatus());
      response.setTimeStamp(walletVerifyResponse.getTimeStamp());
    } else {
      response.setMessage("SET A VALID AGGREGATOR");

    }
    return response;
  }
}
package com.example.paymentapi.clients.Fidelity.service;

import com.example.paymentapi.model.AccountCreditRequest;
import com.example.paymentapi.model.AccountCreditResponse;
import com.example.paymentapi.model.CustomerData;
import com.example.paymentapi.model.StatusQueryRequest;
import com.example.paymentapi.model.StatusQueryResponse;
import com.example.paymentapi.model.WalletCreditRequest;
import com.example.paymentapi.model.WalletDebitRequest;
import com.example.paymentapi.model.WalletDebitResponse;
import com.example.paymentapi.model.WalletDebitResponseGeneral;
import com.example.paymentapi.model.WalletVerifyRequest;
import com.example.paymentapi.model.WalletVerifyResponse;
import com.example.paymentapi.services.TransactionLogService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.ObjectInputFilter.Status;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class FidelityService {

    private static final Logger logger = LoggerFactory.getLogger(FidelityService.class);

    private final String baseUrl;
    private final TokenManager tokenManager;
    private final RestTemplate restTemplate;
    private final TransactionLogService transactionLogService;
    private final ObjectMapper objectMapper;

    public FidelityService(
            @Value("${orange.base-url}") String baseUrl,
            TokenManager tokenManager,
            RestTemplate restTemplate,
            TransactionLogService transactionLogService,
            ObjectMapper objectMapper) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.tokenManager = tokenManager;
        this.restTemplate = restTemplate;
        this.transactionLogService = transactionLogService;
        this.objectMapper = objectMapper;
    }

    /**
     * Generic request handler for Orange API, supporting different request types.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> makehttpRequest(HttpMethod method, String endpoint, Object body) throws Exception {
        String url = baseUrl + endpoint;
        logger.info("Making {} request to {} with body: {}", method, url, body);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenManager.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> entity = (body != null) ? new HttpEntity<>(body, headers) : new HttpEntity<>(headers);

        ResponseEntity<Map> response = null;
        String errorMessage = null;

        try {
            response = restTemplate.exchange(url, method, entity, Map.class);
        } catch (HttpStatusCodeException ex) {
            // ⚠️ Capture error body instead of losing it
            String errorBody = ex.getResponseBodyAsString();
            logger.error("HTTP request failed with status {} and body: {}", ex.getRawStatusCode(), errorBody);

            // Try to parse error body into Map so service can still consume it
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("status", ex.getRawStatusCode());
            errorMap.put("error", ex.getStatusText());
            errorMap.put("message", ex.getResponseBodyAsString());

            return errorMap;
        } catch (Exception ex) {
            logger.error("HTTP request to {} failed: {}", url, ex.getMessage(), ex);
            throw ex;
        }

        int status = (response != null) ? response.getStatusCodeValue() : -1;
        logger.info("Response: status={}, body={}", status, (response != null ? response.getBody() : "N/A"));

        if (response == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new Exception("Request failed with status: " + (response != null ? status : "Unknown"));
        }

        return (Map<String, Object>) response.getBody();
    }

    // Enquiry methods KYC
    public WalletVerifyResponse verifyWallet(WalletVerifyRequest request) throws Exception {
        WalletVerifyResponse response = new WalletVerifyResponse();
        response.setMessage("error in Fidelity service verifywallet method");
        Map<String, Object> verifyWallet = makehttpRequest(HttpMethod.POST, "Enquiry/wallet", request);

        if ("1".equals(String.valueOf(verifyWallet.get("status")))) {

            CustomerData data = new CustomerData();
            Map<String, Object> dataMap = (Map<String, Object>) verifyWallet.get("data");
            if (dataMap != null) {
                data.setName((String) dataMap.get("name"));
            }

            response.setMessage("Success");
            response.setData(data);
            response.setTimeStamp(getString(verifyWallet, "timeStamp"));
            response.setStatus(0);

        } else {
            response.setMessage(getString(verifyWallet, "message"));
            // response.setData(data);
            // response.setTimeStamp(getString(verifyWallet, "timeStamp"));
            response.setStatus(999);
        }
        return response;
    }

    // public Map<String, Object> verifyBankAccount(Object request) throws Exception
    // {
    // return makeRequest(HttpMethod.POST, "Enquiry/customerBankAccount", request);
    // }

    // public Map<String, Object> ghanaCardKYC(Object request) throws Exception {
    // return makeRequest(HttpMethod.POST, "Enquiry/ghanaCardKyc", request);
    // }

    // public Map<String, Object> ghanaCardNoKYC(Object request) throws Exception {
    // return makeRequest(HttpMethod.POST, "Enquiry/ghanaCardNoKyc", request);
    // }

    // public Map<String, Object> getBankList() throws Exception {
    // return makeRequest(HttpMethod.GET, "Enquiry/bankList", null);
    // }

    // public Map<String, Object> getMerchantAccountList() throws Exception {
    // return makeRequest(HttpMethod.GET, "Enquiry/merchantAccountList", null);
    // }

    // public Map<String, Object> getMerchantServiceList() throws Exception {
    // return makeRequest(HttpMethod.GET, "Enquiry/merchantServiceList", null);
    // }

    // Transaction methods
    public WalletDebitResponse debitWallet(WalletDebitRequest request) throws Exception {
        WalletDebitResponse response = new WalletDebitResponse();
        response.setReference(request.getReference());

        response.setMessage("Fidelity default error in debitWallet method");
        response.setStatus(999);
        response.setAmount(request.getAmount());

        logger.info("Inside Fidelity Client Service for Debiting wallet");

        Map<String, Object> debitMap = makehttpRequest(HttpMethod.POST, "Transfer/walletDebit", request);

        if ("3".equals(String.valueOf(debitMap.get("status")))) {

            response.setMessage("PENDING");
            response.setApproveDate(getString(debitMap, "approveDate"));
            response.setTransactionId(getString(debitMap, "transactionId"));
            response.setTransType(getString(debitMap, "transType"));
            response.setRequestDate(getString(debitMap, "requestDate"));
            response.setTimeStamp(getString(debitMap, "timeStamp"));
            response.setStatus(getInteger(debitMap, "status"));

        } else {
            response.setMessage(getString(debitMap, "message"));
            response.setApproveDate(getString(debitMap, "approveDate"));
            response.setTransactionId(getString(debitMap, "transactionId"));
            response.setTransType(getString(debitMap, "transType"));
            response.setRequestDate(getString(debitMap, "requestDate"));
            response.setNarration(getString(debitMap, "narration"));
            response.setTimeStamp(getString(debitMap, "timeStamp"));
            response.setStatus(getInteger(debitMap, "status"));
        }
        logger.info("Response set for : {}", response.toString());
        return response;

    }

    public WalletDebitResponse creditWallet(WalletDebitRequest request) throws Exception {
        WalletDebitResponse response = new WalletDebitResponse();
        response.setReference(request.getReference());

        response.setMessage("Fidelity default error in creditWallet method");
        response.setStatus(999);
        response.setAmount(request.getAmount());

        logger.info("Inside Fidelity Client Service for Crediting wallet");

        Map<String, Object> debitMap = makehttpRequest(HttpMethod.POST, "Transfer/walletDebit", request);

        if ("3".equals(String.valueOf(debitMap.get("status")))) {

            response.setMessage("PENDING");
            response.setApproveDate(getString(debitMap, "approveDate"));
            response.setTransactionId(getString(debitMap, "transactionId"));
            response.setTransType(getString(debitMap, "transType"));
            response.setRequestDate(getString(debitMap, "requestDate"));
            response.setTimeStamp(getString(debitMap, "timeStamp"));
            response.setStatus(getInteger(debitMap, "status"));

        } else {
            response.setMessage(getString(debitMap, "message"));
            response.setApproveDate(getString(debitMap, "approveDate"));
            response.setTransactionId(getString(debitMap, "transactionId"));
            response.setTransType(getString(debitMap, "transType"));
            response.setRequestDate(getString(debitMap, "requestDate"));
            response.setNarration(getString(debitMap, "narration"));
            response.setTimeStamp(getString(debitMap, "timeStamp"));
            response.setStatus(getInteger(debitMap, "status"));
        }
        logger.info("Response set for : {}", response.toString());
        return response;

    }

    public AccountCreditResponse creditAccount(AccountCreditRequest request) throws Exception {
        AccountCreditResponse response = new AccountCreditResponse();
        response.setReference(request.getReference());

        response.setMessage("Fidelity default error in creditAccount method");
        response.setStatus(999);
        response.setAmount(request.getAmount());

        logger.info("Inside Fidelity Client Service for Crediting Account");

        Map<String, Object> debitMap = makehttpRequest(HttpMethod.POST, "Transfer/accountCredit", request);

        if ("3".equals(String.valueOf(debitMap.get("status")))) {

            response.setMessage("PENDING");
            response.setApproveDate(getString(debitMap, "approveDate"));
            response.setTransactionId(getString(debitMap, "transactionId"));
            response.setTransType(getString(debitMap, "transType"));
            response.setRequestDate(getString(debitMap, "requestDate"));
            response.setTimeStamp(getString(debitMap, "timeStamp"));
            response.setStatus(getInteger(debitMap, "status"));

        } else {
            response.setMessage(getString(debitMap, "message"));
            response.setApproveDate(getString(debitMap, "approveDate"));
            response.setTransactionId(getString(debitMap, "transactionId"));
            response.setTransType(getString(debitMap, "transType"));
            response.setRequestDate(getString(debitMap, "requestDate"));
            response.setNarration(getString(debitMap, "narration"));
            response.setTimeStamp(getString(debitMap, "timeStamp"));
            response.setStatus(getInteger(debitMap, "status"));
        }
        logger.info("Response set for : {}", response.toString());
        return response;

    }

    public StatusQueryResponse checkStatus(StatusQueryRequest request) throws Exception {
        StatusQueryResponse response = new StatusQueryResponse();

        Map<String, Object> statusMap = makehttpRequest(HttpMethod.POST, "Transfer/statusCheck", request);

        Integer status = getInteger(statusMap, "status");

        if (status != null && status != 3) {
            response.setMessage(getString(statusMap, "message"));

            if (status == 1) {
                response.setMessage("Success");
                status = 0;

            }
            response.setApproveDate(getString(statusMap, "approveDate"));
            response.setTransactionId(getString(statusMap, "transactionId"));
            response.setTransType(getString(statusMap, "transType"));
            response.setRequestDate(getString(statusMap, "requestDate"));
            response.setTimeStamp(getString(statusMap, "timeStamp"));
            response.setReference(getString(statusMap, "reference"));
            response.setNarration(getString(statusMap, "narration"));
            response.setBeneficiary(getString(statusMap, "beneficiary"));
            logger.info("Update from Fidelity  **********  " + (getString(statusMap, "message")));

        } else {
            logger.info("Still  **********  " + (getString(statusMap, "message")));
            response.setMessage(getString(statusMap, "message"));
            response.setApproveDate(getString(statusMap, "approveDate"));
            response.setTransactionId(getString(statusMap, "transactionId"));
            response.setTransType(getString(statusMap, "transType"));
            response.setRequestDate(getString(statusMap, "requestDate"));
            response.setTimeStamp(getString(statusMap, "timeStamp"));
            response.setStatus(getInteger(statusMap, "status"));
            response.setReference(getString(statusMap, "reference"));
            response.setNarration(getString(statusMap, "narration"));
            response.setBeneficiary(getString(statusMap, "beneficiary"));
        }

        return response;
    }

    // Helper method to safely get a String from the map
    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    // Helper method to safely get a BigDecimal from the map
    private BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            logger.warn("Missing or null value for key: {}", key);
            return null;
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            logger.error("Invalid number format for key {}: {}", key, value, e);
            return null;
        }
    }

    // Helper method to safely get an Integer from the map
    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            logger.warn("Missing or null value for key: {}", key);
            return null;
        }
        try {
            return Integer.valueOf(value.toString());
        } catch (NumberFormatException e) {
            logger.error("Invalid integer format for key {}: {}", key, value, e);
            return null;
        }
    }

}
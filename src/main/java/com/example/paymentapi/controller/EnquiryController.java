package com.example.paymentapi.controller;

import com.example.paymentapi.clients.Fidelity.controller.FidelityController;
import com.example.paymentapi.clients.Fidelity.service.FidelityService;
import com.example.paymentapi.model.*;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/enquiry")
public class EnquiryController {

    private static final Logger logger = LoggerFactory.getLogger(EnquiryController.class);

    @Autowired
    private FidelityController fidelityController;

    @PostMapping("/wallet")
    public WalletKYCResponse verifyWallet(@Valid @RequestBody WalletKYCRequest request) throws Exception {
        WalletKYCResponse response = new WalletKYCResponse();
        logger.info("Received wallet verification request: {}", request);

        if (request.getAggregator()!=null && request.getAggregator().equals("FIDELITY")) {
            return fidelityController.verifyWallet(request);
        } else {
            response.setMessage("SET A VALID AGGREGATOR");
        }
        return response;

    }

    // @PostMapping("/customerBankAccount")
    // public Map<String, Object> verifyBankAccount(@Valid @RequestBody
    // BankAccountVerifyRequest request) throws Exception {
    // logger.info("Received bank account verification request: {}", request);
    // return orangeProvider.verifyBankAccount(request);
    // }

    // @PostMapping("/ghanaCardKyc")
    // public Map<String, Object> ghanaCardKYC(@Valid @RequestBody
    // GhanaCardKYCRequest request) throws Exception {
    // return orangeProvider.ghanaCardKYC(request);
    // }

    // @PostMapping("/ghanaCardNoKyc")
    // public Map<String, Object> ghanaCardNoKYC(@Valid @RequestBody
    // GhanaCardNoKYCRequest request) throws Exception {
    // return orangeProvider.ghanaCardNoKYC(request);
    // }

    // @GetMapping("/bankList")
    // public Map<String, Object> getBankList() throws Exception {
    // return orangeProvider.getBankList();
    // }

    // @GetMapping("/merchantAccountList")
    // public Map<String, Object> getMerchantAccountList() throws Exception {
    // return orangeProvider.getMerchantAccountList();
    // }

    // @GetMapping("/merchantServiceList")
    // public Map<String, Object> getMerchantServiceList() throws Exception {
    // return orangeProvider.getMerchantServiceList();
    // }

    // @ExceptionHandler(Exception.class)
    // public Map<String, Object> handleException(Exception e) {
    // return Map.of(
    // "status", 0,
    // "message", e.getMessage()
    // );
    // }
}
package com.example.paymentapi.controller;

import com.example.paymentapi.clients.Fidelity.controller.FidelityController;
import com.example.paymentapi.model.*;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/transfer")
public class TransferController {

    private static final Logger logger = LoggerFactory.getLogger(TransferController.class);

    @Autowired
    private FidelityController fidelityController;

    @PostMapping("/walletDebit")
    public WalletDebitResponseGeneral debitWallet(@Valid @RequestBody WalletDebitRequestGeneral request)
            throws Exception {
        logger.info("Received wallet debit request: {}", request);

        if (request.getAggregator().equals("FIDELITY")) {

            return fidelityController.debitCustomerWallet(request);
        } else {
            WalletDebitResponseGeneral responseGenerals = new WalletDebitResponseGeneral();
            responseGenerals.setMessage("SET A VALID AGGREGATOR");
            return responseGenerals;
        }
    }

    @PostMapping("/walletCredit")
    public WalletDebitResponseGeneral creditWallet(@Valid @RequestBody WalletDebitRequestGeneral request)
            throws Exception {
        logger.info("Received wallet credit request: {}", request);

        if (request.getAggregator().equals("FIDELITY")) {

            return fidelityController.debitCustomerWallet(request);
        } else {
            WalletDebitResponseGeneral responseGenerals = new WalletDebitResponseGeneral();
            responseGenerals.setMessage("SET A VALID AGGREGATOR");
            return responseGenerals;
        }
    }

    @PostMapping("/accountCredit")
    public AccountCreditResponseGeneral creditAccount(@Valid @RequestBody AccountCreditRequestGeneral request)
            throws Exception {
        logger.info("Received account credit request: {}", request);

        if (request.getAggregator().equals("FIDELITY")) {

            return fidelityController.creditAccount(request);
            
        } else {
            AccountCreditResponseGeneral responseGenerals = new AccountCreditResponseGeneral();
            responseGenerals.setMessage("SET A VALID AGGREGATOR");
            return responseGenerals;
        }

    }

    @PostMapping("/statusCheck")
    public StatusQueryResponse checkStatus(@Valid @RequestBody StatusQueryRequest request) throws Exception {
        logger.info("Received status check request: {}", request);
        if (request.getAggregator().equals("FIDELITY")) {

            return fidelityController.checkStatus(request);
        } else {
            StatusQueryResponse responseGenerals = new StatusQueryResponse();
            responseGenerals.setStatus(999);
            responseGenerals.setMessage("SET A VALID AGGREGATOR");
            return responseGenerals;
        }
    }

    // @ExceptionHandler(Exception.class)
    // public Map<String, Object> handleException(Exception e) {
    // return Map.of(
    // "status", 0,
    // "message", e.getMessage());
    // }
}
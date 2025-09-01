package com.example.paymentapi.clients.Fidelity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TokenManager {

    private static final Logger logger = LoggerFactory.getLogger(TokenManager.class);

    private final ReentrantLock lock = new ReentrantLock();

    private String token;
    private LocalDateTime expiry;
    private final String baseUrl;
    private final String secretKey;
    private final String secretToken;
    private final long tokenExpiryMargin;
    private final RestTemplate restTemplate;

    public TokenManager(
        @Value("${orange.base-url}") String baseUrl,
        @Value("${orange.secret-key}") String secretKey,
        @Value("${orange.secret-token}") String secretToken,
        @Value("${orange.token-expiry-margin:300}") long tokenExpiryMargin,
        RestTemplate restTemplate
    ) {
        this.baseUrl = baseUrl;
        this.secretKey = secretKey;
        this.secretToken = secretToken;
        this.tokenExpiryMargin = tokenExpiryMargin;
        this.restTemplate = restTemplate;
    }
    public String getToken() throws Exception {
        logger.debug("Acquiring lock to get token");
        lock.lock();
        try {
            if (token == null) {
                logger.info("GETTING TOKEN..");
                refreshToken();
            } else {
                logger.debug("Token is valid. Returning existing token.");
            }
            return token;
        } finally {
            lock.unlock();
            logger.debug("Lock released after getting token");
        }
    }

    private boolean isTokenExpired() {
        if (expiry == null) {
            logger.debug("Token expiry is null, token considered expired.");
            return true;
        }
        boolean expired = LocalDateTime.now().isAfter(expiry.minusSeconds(tokenExpiryMargin));
        logger.debug("Token expiry check: now={}, expiry={}, margin={}, expired={}",
                LocalDateTime.now(), expiry, tokenExpiryMargin, expired);
        return expired;
    }

    private void refreshToken() throws Exception {
        logger.info("Refreshing token from Orange API...with .. "+secretKey+" ... "+secretToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("secretKey", secretKey);
        headers.set("secretToken", secretToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        logger.info("Sending token request to: {}Auth/token", baseUrl);

        ResponseEntity<Map> response;
        try {
            response = restTemplate.exchange(
                    baseUrl + "Auth/token",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
        } catch (Exception ex) {
            logger.error("Exception during token request: {}", ex.getMessage(), ex);
            throw ex;
        }

        logger.debug("Received response: status={}, body={}", response.getStatusCodeValue(), response.getBody());

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> body = response.getBody();
            if (body != null && (Integer) body.get("status") == 1) {
                Map<String, String> data = (Map<String, String>) body.get("data");
                this.token = data.get("token");
                this.expiry = LocalDateTime.parse(data.get("expiry"), DateTimeFormatter.ISO_DATE_TIME);
                logger.info("Token refreshed successfully. New expiry: {}", this.expiry);
            } else {
                logger.warn("Failed to obtain token: {}", body != null ? body.get("message") : "No body");
                throw new Exception("Failed to obtain token: " + (body != null ? body.get("message") : "No body"));
            }
        } else {
            logger.error("Failed to fetch token: HTTP {}", response.getStatusCodeValue());
            throw new Exception("Failed to fetch token: HTTP " + response.getStatusCodeValue());
        }
    }
}
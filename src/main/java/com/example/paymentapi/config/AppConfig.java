package com.example.paymentapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.example.paymentapi.clients.Fidelity.service.FidelityService;
import com.example.paymentapi.clients.Fidelity.service.TokenManager;

@Configuration
public class AppConfig {

    @Value("${orange.base-url}")
    private String orangeBaseUrl;

    @Value("${orange.secret-key}")
    private String secretKey;

    @Value("${orange.secret-token}")
    private String secretToken;

    @Value("${orange.token-expiry-margin:300}")
    private long tokenExpiryMargin;

    @Value("${provider:orange}")
    private String provider;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public TokenManager tokenManager(RestTemplate restTemplate) {
        return new TokenManager(orangeBaseUrl, secretKey, secretToken, tokenExpiryMargin, restTemplate);
    }

   
}
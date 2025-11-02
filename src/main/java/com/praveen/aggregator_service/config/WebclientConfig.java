package com.praveen.aggregator_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebclientConfig {

    @Bean
    public WebClient customerWebClient(@Value("${customer.service.url}") String customerServiceUrl) {
        return WebClient
                .builder()
                .baseUrl(customerServiceUrl)
                .build();
    }

    @Bean
    public WebClient stockWebClient(@Value("${stock.service.url}") String stockServiceUrl) {
        return WebClient
                .builder()
                .baseUrl(stockServiceUrl)
                .build();
    }
}

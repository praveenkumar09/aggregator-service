package com.praveen.aggregator_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebclientConfig {

    @Bean
    public WebClient customerWebClient() {
        return WebClient
                .builder()
                .baseUrl("http://localhost:6060/customers")
                .build();
    }

    @Bean
    public WebClient stockWebClient() {
        return WebClient
                .builder()
                .baseUrl("http://localhost:7070/stock")
                .build();
    }
}

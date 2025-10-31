package com.praveen.aggregator_service.service;

import com.praveen.aggregator_service.domain.Ticker;
import com.praveen.aggregator_service.dto.PriceStreamResponse;
import com.praveen.aggregator_service.dto.StockPriceResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StockService {

    private final WebClient stockWebClient;

    public StockService(WebClient stockWebClient) {
        this.stockWebClient = stockWebClient;
    }

    public Mono<StockPriceResponse> getStockPrice(Ticker ticker) {
        return stockWebClient.get()
                .uri("/{ticker}", ticker)
                .retrieve()
                .bodyToMono(StockPriceResponse.class);
    }

    public Flux<PriceStreamResponse> getPriceStreamResponse(){
        return stockWebClient.get()
                .uri("/price-stream")
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(PriceStreamResponse.class)
                .onBackpressureBuffer();
    }

}

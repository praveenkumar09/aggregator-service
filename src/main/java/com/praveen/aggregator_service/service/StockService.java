package com.praveen.aggregator_service.service;

import com.praveen.aggregator_service.domain.Ticker;
import com.praveen.aggregator_service.dto.PriceStreamResponse;
import com.praveen.aggregator_service.dto.StockPriceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Objects;

@Service
public class StockService {
    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    private final WebClient stockWebClient;

    private Flux<PriceStreamResponse> priceStreamResponseFlux;

    public StockService(WebClient stockWebClient) {
        this.stockWebClient = stockWebClient;
    }

    public Mono<StockPriceResponse> getStockPrice(Ticker ticker) {
        return stockWebClient.get()
                .uri("/stock/{ticker}", ticker)
                .retrieve()
                .bodyToMono(StockPriceResponse.class);
    }

    public Flux<PriceStreamResponse> getPriceStream(){
        if(Objects.isNull(priceStreamResponseFlux)){
            priceStreamResponseFlux = getPriceStreamResponse();
        }
        return priceStreamResponseFlux;
    }

    private Flux<PriceStreamResponse> getPriceStreamResponse(){
        return stockWebClient.get()
                .uri("/stock/price-stream")
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(PriceStreamResponse.class)
                .retryWhen(retryPriceStream())
                .cache(1);
    }

    private Retry retryPriceStream(){
        return Retry
                .fixedDelay(100, Duration.ofSeconds(1))
                .doBeforeRetry(retrySignal -> logger.error("Retrying to get price stream : {}", retrySignal.failure().getMessage()));
    }

}

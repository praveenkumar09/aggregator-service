package com.praveen.aggregator_service.service;


import com.praveen.aggregator_service.dto.*;
import com.praveen.aggregator_service.mapper.TradeRequestMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.function.Function;

@Service
public class CustomerService {

    private final WebClient customerWebClient;

    private final StockService stockService;

    public CustomerService(WebClient customerWebClient, StockService stockService) {
        this.customerWebClient = customerWebClient;
        this.stockService = stockService;
    }

    public Mono<CustomerPortfolioResponse> getCustomerPortfolio(Integer id){
        return customerWebClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(CustomerPortfolioResponse.class);
    }

    public Mono<TradeResponse> trade(Integer customerId, TradeRequest tradeRequest){
        return Mono.zip(this.stockService.getStockPrice(tradeRequest.ticker()),
                Mono.just(tradeRequest))
                .transform(transformTupleToMono())
                .flatMap(stockTradeRequest -> executeTrade(customerId, stockTradeRequest));
    }

    private Mono<TradeResponse> executeTrade(Integer customerId, StockTradeRequest stockTradeRequest) {
        return customerWebClient.post()
                .uri("/{customerId}/trade", customerId)
                .bodyValue(stockTradeRequest)
                .retrieve()
                .bodyToMono(TradeResponse.class);
    }

    private Function<Mono<Tuple2<StockPriceResponse, TradeRequest>>, Mono<StockTradeRequest>> transformTupleToMono(){
        return tuple2Mono -> tuple2Mono
                .map(tuple ->
                        TradeRequestMapper
                                .toStockTradeRequest(
                                        tuple.getT1(),
                                        tuple.getT2()
                                ));
    }

}

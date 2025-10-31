package com.praveen.aggregator_service.service;


import com.praveen.aggregator_service.dto.CustomerPortfolioResponse;
import com.praveen.aggregator_service.dto.TradeRequest;
import com.praveen.aggregator_service.dto.TradeResponse;
import com.praveen.aggregator_service.mapper.TradeRequestMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
                .map(responseTuple -> TradeRequestMapper
                        .toStockTradeRequest(
                                responseTuple.getT1(),
                                responseTuple.getT2()
                        )
                )
                .flatMap(stockTradeRequest -> this.customerWebClient.post()
                        .uri("/{customerId}/trade", customerId)
                        .bodyValue(stockTradeRequest)
                        .retrieve()
                        .bodyToMono(TradeResponse.class));
    }
}

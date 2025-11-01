package com.praveen.aggregator_service.controller;

import com.praveen.aggregator_service.domain.Ticker;
import com.praveen.aggregator_service.dto.*;
import com.praveen.aggregator_service.service.CustomerService;
import com.praveen.aggregator_service.service.StockService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class AggregateController {

    private final StockService stockService;


    private final CustomerService customerService;


    public AggregateController(StockService stockService, CustomerService customerService) {
        this.stockService = stockService;
        this.customerService = customerService;
    }

    @GetMapping("/stock/{ticker}")
    public Mono<StockPriceResponse> getStockPrice(@PathVariable Ticker ticker) {
        return this
                .stockService
                .getStockPrice(ticker);
    }

    @GetMapping(value = "stock/price-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<PriceStreamResponse> getPriceStream(){
        return this.stockService
                .getPriceStreamResponse();
    }

    @GetMapping("customers/{id}")
    public Mono<CustomerPortfolioResponse> getCustomerPortfolio(@PathVariable Integer id){
        return this.customerService.getCustomerPortfolio(id);
    }

    @PostMapping("customers/{customerId}/trade")
    public Mono<TradeResponse> trade(
            @PathVariable Integer customerId,
            @Valid @RequestBody TradeRequest tradeRequest){
        return customerService.trade(customerId, tradeRequest);
    }


}

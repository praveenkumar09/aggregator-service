package com.praveen.aggregator_service.controller;

import com.praveen.aggregator_service.domain.Ticker;
import com.praveen.aggregator_service.dto.PriceStreamResponse;
import com.praveen.aggregator_service.dto.StockPriceResponse;
import com.praveen.aggregator_service.service.StockService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("sample")
public class SampleController {

    private final StockService stockService;


    public SampleController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/stock/{ticker}")
    public Mono<StockPriceResponse> getStockPrice(@PathVariable Ticker ticker) {
        return this
                .stockService
                .getStockPrice(ticker);
    }

    @GetMapping(value = "/price-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<PriceStreamResponse> getPriceStream(){
        return this.stockService
                .getPriceStreamResponse();
    }

}

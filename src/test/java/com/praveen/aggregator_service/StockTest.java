package com.praveen.aggregator_service;

import com.praveen.aggregator_service.config.WebclientConfig;
import com.praveen.aggregator_service.dto.PriceStreamResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class StockTest extends AbstractIntegrationTest{
    private static final Logger logger = LoggerFactory.getLogger(StockTest.class);

    private void testMockStock(String apiPath, String expectedResponse, int statusCode){
        this.mockServerClient
                .when(HttpRequest.request(apiPath))
                .respond(HttpResponse.response(expectedResponse)
                        .withStatusCode(statusCode)
                        .withContentType(MediaType.APPLICATION_JSON)
                );
    }

    private void testMockStockStream(String apiPath, String expectedResponse, int statusCode){
        this.mockServerClient
                .when(HttpRequest.request(apiPath))
                .respond(HttpResponse.response(expectedResponse)
                        .withStatusCode(statusCode)
                        .withContentType(MediaType.parse("application/x-ndjson"))
                );
    }
    
    private UnaryOperator<String> getResponseBody(){
        return this::resourcesToString;
    }
    
    private BiFunction<HttpStatus, String, WebTestClient.BodyContentSpec> getStockPrice(){
        return (httpStatus,path) -> this
                .webTestClient
                .get()
                .uri(path)
                .exchange()
                .expectStatus().isEqualTo(httpStatus)
                .expectBody()
                .consumeWith(entityExchangeResult -> logger.info("Response : {}", new String(Objects.requireNonNull(entityExchangeResult.getResponseBody()))));
    }

    private BiConsumer<HttpStatus, String> getStockPriceStream(){
        return (httpStatus,path) -> this
                .webTestClient
                .get()
                .uri(path)
                .accept(org.springframework.http.MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isEqualTo(httpStatus)
                .returnResult(PriceStreamResponse.class)
                .getResponseBody()
                .doOnNext(price -> logger.info("Price : {}", price))
                .as(StepVerifier::create)
                .assertNext(p -> Assertions.assertEquals(4,p.price()))
                .assertNext(p -> Assertions.assertEquals(5,p.price()))
                .assertNext(p -> Assertions.assertEquals(6,p.price()))
                .expectComplete()
                .verify();

    }

    @Test
    public void testStockPrice(){
        this.testMockStock("/stock/APPLE",
                this.getResponseBody().apply("stock-service/stock-price-200.json"),
                HttpStatus.OK.value());
        this.getStockPrice().apply(HttpStatus.OK, "/stock/APPLE")
                .jsonPath("$.ticker").isEqualTo("APPLE")
                .jsonPath("$.price").isEqualTo(92);
    }

    @Test
    public void testStockPriceStream(){
        this.testMockStockStream("/stock/price-stream",
                this.getResponseBody().apply("stock-service/stock-stream-200.jsonl"),
                HttpStatus.OK.value());
        this.getStockPriceStream().accept(HttpStatus.OK,"/stock/price-stream");
    }

}

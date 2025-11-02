package com.praveen.aggregator_service;

import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

public class CustomerTest extends AbstractIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(CustomerTest.class);

    @Test
    public void test_CustomerInformation() {
        //given
        mockCustomerInformation(
                "customer-service/customer-information-200.json",
                "/customers/1",
                200
        );

        //then
        getCustomerInformation(HttpStatus.OK)
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Sam")
                .jsonPath("$.balance").isEqualTo(10000)
                .jsonPath("$.holdings").isEmpty();
    }

    private void mockCustomerInformation(String resourcePath, String path, int responseCode){
        var responseBody = this.resourcesToString(resourcePath);
        logger.info("Response Body : {}", responseBody);
        mockServerClient
                .when(HttpRequest.request(path))
                .respond(
                        HttpResponse.response(responseBody)
                                .withStatusCode(responseCode)
                                .withContentType(MediaType.APPLICATION_JSON)
                );
    }

    private WebTestClient.BodyContentSpec getCustomerInformation(HttpStatus httpStatus){
        return this.webTestClient.get()
                .uri("/customers/1")
                .exchange()
                .expectStatus().isEqualTo(httpStatus)
                .expectBody()
                .consumeWith(entityExchangeResult -> logger.info("Response : {}", new String(Objects.requireNonNull(entityExchangeResult.getResponseBody()))));
    }

}

package com.praveen.aggregator_service.error;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import reactor.core.publisher.Mono;

public class AggregatorErrorHandler {

    public static Mono<Error> handleError(ProblemDetail problemDetail) {
        return Mono.error(new AggregatorException(problemDetail));
    }

}

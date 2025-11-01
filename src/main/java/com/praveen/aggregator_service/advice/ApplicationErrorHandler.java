package com.praveen.aggregator_service.advice;

import com.praveen.aggregator_service.error.AggregatorException;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Consumer;

@ControllerAdvice
public class ApplicationErrorHandler {

    @ExceptionHandler(AggregatorException.class)
    public ProblemDetail handleAggregatorException(AggregatorException exception) {
        return exception.getProblemDetail();
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ServerResponse> handleValidationException(
            WebExchangeBindException exception,
            ServerRequest request
    ) {
        var errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return generateProblemDetail(HttpStatus.BAD_REQUEST,exception, request, problemDetail -> {
            problemDetail.setType(URI.create("http://example.com/problems/bad-request"));
            problemDetail.setDetail("Invalid Input: " + String.join(", ", errors) + ".");
        });
    }


    @ExceptionHandler(DecodingException.class)
    public Mono<ServerResponse> handleInvalidEnum(
            DecodingException exception,
            ServerRequest request
    ) {
        String message = exception.getMessage();

        // Extract friendly error message for invalid enum values
        if (message != null && message.contains("Ticker")) {
            message = "Invalid ticker value. Allowed values: APPLE, GOOGLE, MICROSOFT, AMAZON";
        } else if (message != null && message.contains("TradeAction")) {
            message = "Invalid trade action. Please provide a valid action";
        } else {
            message = "Invalid request body format";
        }

        String finalMessage = message;
        return generateProblemDetail(
                HttpStatus.BAD_REQUEST,
                exception,
                request,
                problemDetail -> {
                    problemDetail.setType(URI.create("http://example.com/problems/bad-request"));
                    problemDetail.setDetail("Invalid input: "+ finalMessage);
                });
    }

    private Mono<ServerResponse> generateProblemDetail(HttpStatus httpStatus, Exception exception, ServerRequest request, Consumer<ProblemDetail> consumer) {
        var problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, exception.getMessage());
        problemDetail.setTitle(exception.getClass().getSimpleName());
        problemDetail.setInstance(URI.create(request.uri().toString()));
        consumer.accept(problemDetail);
        return ServerResponse.status(httpStatus).bodyValue(problemDetail);
    }

}

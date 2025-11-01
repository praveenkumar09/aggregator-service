package com.praveen.aggregator_service.advice;

import com.praveen.aggregator_service.error.AggregatorException;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import java.net.URI;
import java.util.function.Consumer;

@RestControllerAdvice
public class ApplicationErrorHandler {

    @ExceptionHandler(AggregatorException.class)
    public ProblemDetail handleAggregatorException(AggregatorException exception) {
        return exception.getProblemDetail();
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ProblemDetail handleValidationException(
            ServerWebInputException exception
    ) {
        System.out.println("WebExchangeBindException is triggered");
        System.out.println("Exception class: " + exception.getClass().getName());
        System.out.println("Exception message: " + exception.getMessage());

        return generateProblemDetail(HttpStatus.BAD_REQUEST,exception, problemDetail -> {
            problemDetail.setType(URI.create("http://example.com/problems/bad-request"));
            problemDetail.setProperty("error","Invalid Input :" + exception.getMessage() + ".");
        });
    }


    @ExceptionHandler(DecodingException.class)
    public ProblemDetail handleInvalidEnum(
            DecodingException exception
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
                problemDetail -> {
                    problemDetail.setType(URI.create("http://example.com/problems/bad-request"));
                    problemDetail.setProperty("error","Invalid input: "+ finalMessage);
                });
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(
            Exception exception
    ) {
        System.out.println("Generic exception is triggered");
        System.out.println("Exception class: " + exception.getClass().getName());
        System.out.println("Exception message: " + exception.getMessage());

        return generateProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception,
                problemDetail -> {
                    problemDetail.setType(URI.create("http://example.com/problems/internal-server-error"));
                    problemDetail.setProperty("error","An unexpected error occurred. Please try again later.");
                });
    }


    private ProblemDetail generateProblemDetail(HttpStatus httpStatus, Exception exception, Consumer<ProblemDetail> consumer) {
        var problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, exception.getMessage());
        problemDetail.setTitle(exception.getClass().getSimpleName());
        problemDetail.setInstance(URI.create("/" + exception.getClass().getSimpleName().replace("Exception", "")));
        consumer.accept(problemDetail);
        return problemDetail;
    }

}

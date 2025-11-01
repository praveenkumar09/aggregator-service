package com.praveen.aggregator_service.advice;

import com.praveen.aggregator_service.error.AggregatorException;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.function.Consumer;

@ControllerAdvice
public class ApplicationErrorHandler {

    @ExceptionHandler(AggregatorException.class)
    public ProblemDetail handleAggregatorException(AggregatorException exception) {
        return exception.getProblemDetail();
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ProblemDetail handleValidationException(WebExchangeBindException exception) {
        String detail = "Validation failed";

        var errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return generateProblemDetail(HttpStatus.BAD_REQUEST,detail, problemDetail -> problemDetail.setProperty("errors", errors));
    }


    @ExceptionHandler(DecodingException.class)
    public ProblemDetail handleInvalidEnum(DecodingException exception) {
        String message = exception.getMessage();

        // Extract friendly error message for invalid enum values
        if (message != null && message.contains("Ticker")) {
            message = "Invalid ticker value. Allowed values: APPLE, GOOGLE, MICROSOFT, AMAZON";
        } else if (message != null && message.contains("TradeAction")) {
            message = "Invalid trade action. Please provide a valid action";
        } else {
            message = "Invalid request body format";
        }

        return generateProblemDetail(HttpStatus.BAD_REQUEST, message, problemDetail -> {});
    }

    private ProblemDetail generateProblemDetail(HttpStatus httpStatus, String detail, Consumer<ProblemDetail> consumer) {
        var problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, detail);
        consumer.accept(problemDetail);
        return problemDetail;
    }

}

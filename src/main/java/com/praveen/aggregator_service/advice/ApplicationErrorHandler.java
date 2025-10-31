package com.praveen.aggregator_service.advice;

import com.praveen.aggregator_service.error.AggregatorException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationErrorHandler {

    @ExceptionHandler(AggregatorException.class)
    public ProblemDetail handleAggregatorException(AggregatorException exception) {
        return exception.getProblemDetail();
    }
}

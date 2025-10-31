package com.praveen.aggregator_service.error;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

public class AggregatorException extends RuntimeException{

    private final ProblemDetail problemDetail;

    public AggregatorException(ProblemDetail problemDetail) {
        super(problemDetail.getDetail());
        this.problemDetail = problemDetail;
    }

    public ProblemDetail getProblemDetail() {
        return problemDetail;
    }
}

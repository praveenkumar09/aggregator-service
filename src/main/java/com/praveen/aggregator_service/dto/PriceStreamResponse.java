package com.praveen.aggregator_service.dto;

import com.praveen.aggregator_service.domain.Ticker;

import java.time.Instant;
import java.time.LocalDateTime;

public record PriceStreamResponse(Ticker ticker,
                                  Integer price,
                                  LocalDateTime time) {
}

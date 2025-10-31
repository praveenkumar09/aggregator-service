package com.praveen.aggregator_service.dto;

import com.praveen.aggregator_service.domain.Ticker;

public record Holdings(Ticker ticker,
                       Integer quantity) {
}

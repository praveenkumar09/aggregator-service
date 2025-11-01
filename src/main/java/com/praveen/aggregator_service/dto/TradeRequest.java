package com.praveen.aggregator_service.dto;

import com.praveen.aggregator_service.domain.Ticker;
import com.praveen.aggregator_service.domain.TradeAction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TradeRequest(

        @NotNull(message = "Ticker cannot be null")
        Ticker ticker,

        @NotNull(message = "Action cannot be null")
        TradeAction action,

        @NotNull(message = "Quantity cannot be null")
        @Positive(message = "Quantity must be positive")
        Integer quantity) {
}

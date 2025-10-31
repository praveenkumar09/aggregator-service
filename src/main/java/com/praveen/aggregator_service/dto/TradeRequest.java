package com.praveen.aggregator_service.dto;

import com.praveen.aggregator_service.domain.Ticker;
import com.praveen.aggregator_service.domain.TradeAction;

public record TradeRequest(Ticker ticker,
                           TradeAction action,
                           Integer quantity) {
}

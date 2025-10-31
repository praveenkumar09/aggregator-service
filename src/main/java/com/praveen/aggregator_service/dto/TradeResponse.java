package com.praveen.aggregator_service.dto;

import com.praveen.aggregator_service.domain.Ticker;
import com.praveen.aggregator_service.domain.TradeAction;

public record TradeResponse(Integer customerId,
                            Ticker ticker,
                            Integer price,
                            Integer quantity,
                            TradeAction action,
                            Integer totalPrice,
                            Integer balance){
}
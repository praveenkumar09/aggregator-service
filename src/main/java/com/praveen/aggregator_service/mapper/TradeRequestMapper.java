package com.praveen.aggregator_service.mapper;

import com.praveen.aggregator_service.dto.StockPriceResponse;
import com.praveen.aggregator_service.dto.StockTradeRequest;
import com.praveen.aggregator_service.dto.TradeRequest;

public class TradeRequestMapper {

    public static StockTradeRequest toStockTradeRequest(StockPriceResponse stockPriceResponse,
                                                        TradeRequest tradeRequest) {
        return new StockTradeRequest(stockPriceResponse.ticker(),
                stockPriceResponse.price(),
                tradeRequest.quantity(),
                tradeRequest.action());
    }
}

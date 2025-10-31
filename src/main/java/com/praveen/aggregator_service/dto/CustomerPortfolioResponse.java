package com.praveen.aggregator_service.dto;

import java.util.List;

public record CustomerPortfolioResponse(Integer id,
                                        String name,
                                        Integer balance,
                                        List<Holdings> holdings) {
}

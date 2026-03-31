package com.crypto.trading.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TradeRequest {
    private String symbol;   // BTCUSDT hoặc ETHUSDT
    private String type;     // BUY hoặc SELL
    private BigDecimal quantity;
}
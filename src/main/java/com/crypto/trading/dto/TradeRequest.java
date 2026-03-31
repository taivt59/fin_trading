package com.crypto.trading.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TradeRequest {
    private String symbol;   // BTCUSDT or ETHUSDT
    private String type;     // BUY or SELL
    private BigDecimal quantity;
}
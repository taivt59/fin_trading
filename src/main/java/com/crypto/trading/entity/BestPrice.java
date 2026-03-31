package com.crypto.trading.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


@Entity @Data
public class BestPrice {
    @Id
    private String symbol; // ETHUSDT, BTCUSDT
    private BigDecimal bestBid; // For SELL
    private BigDecimal bestAsk; // For BUY
    private LocalDateTime updatedAt;
}
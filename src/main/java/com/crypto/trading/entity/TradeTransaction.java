package com.crypto.trading.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;


@Entity @Data
public class TradeTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String symbol; // ETHUSDT, BTCUSDT
    private String type;   // BUY, SELL
    private BigDecimal quantity;
    private BigDecimal price;
    private LocalDateTime timestamp;
}
package com.crypto.trading.entity;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class HuobiTicker {
    private String symbol; 
    private BigDecimal bid;
    private BigDecimal ask;
}
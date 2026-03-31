package com.crypto.trading.entity;

import lombok.Data;

@Data
public class BinanceTicker {
    private String symbol;
    private String bidPrice;
    private String askPrice;
}
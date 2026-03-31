package com.crypto.trading.entity;

import lombok.Data;

@Data
public class BinanceTicker {
    private String symbol;
    private String bidPrice; // Giá người ta muốn MUA (mình SELL cho họ)
    private String askPrice; // Giá người ta muốn BÁN (mình BUY từ họ)
}
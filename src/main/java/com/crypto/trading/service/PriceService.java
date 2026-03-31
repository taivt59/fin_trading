package com.crypto.trading.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.crypto.trading.entity.BestPrice;
import com.crypto.trading.repository.BestPriceRepository;

@Service
public class PriceService {
    @Autowired 
    private BestPriceRepository repository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRate = 10000) // 10 giây một lần
    public void aggregatePrices() {
        updateBestPrice("BTCUSDT");
        updateBestPrice("ETHUSDT");
    }

    private void updateBestPrice(String symbol) {
        // Giả sử logic call API và so sánh (Simplified)
        // Binance: https://api.binance.com/api/v3/ticker/bookTicker?symbol=BTCUSDT
        // Huobi: https://api.huobi.pro/market/tickers
        
        // Logic: 
        // Best Ask = Min(Binance Ask, Huobi Ask) -> Dùng để Mua
        // Best Bid = Max(Binance Bid, Huobi Bid) -> Dùng để Bán
        
        BestPrice price = new BestPrice();
        price.setSymbol(symbol);
        price.setBestAsk(new BigDecimal("50000.00")); // Giá mẫu
        price.setBestBid(new BigDecimal("49990.00")); // Giá mẫu
        price.setUpdatedAt(LocalDateTime.now());
        repository.save(price);
    }
}
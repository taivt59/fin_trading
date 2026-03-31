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

    @Scheduled(fixedRate = 10000)
    public void aggregatePrices() {
        updateBestPrice("BTCUSDT");
        updateBestPrice("ETHUSDT");
    }

    private void updateBestPrice(String symbol) {
        
        
        BestPrice price = new BestPrice();
        price.setSymbol(symbol);
        price.setBestAsk(new BigDecimal("50000.00"));
        price.setBestBid(new BigDecimal("49990.00"));
        price.setUpdatedAt(LocalDateTime.now());
        repository.save(price);
    }
}
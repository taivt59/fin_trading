package com.crypto.trading.scheduler;

import com.crypto.trading.dto.HuobiResponse;
import com.crypto.trading.entity.BestPrice;
import com.crypto.trading.entity.BinanceTicker;
import com.crypto.trading.entity.HuobiTicker;
import com.crypto.trading.repository.BestPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class PriceScheduler {

    @Autowired
    private BestPriceRepository priceRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    
    private final String BINANCE_URL = "https://api.binance.com/api/v3/ticker/bookTicker?symbol=";
    private final String HUOBI_URL = "https://api.huobi.pro/market/tickers";

    @Scheduled(fixedRate = 10000)
    public void fetchAndAggregatePrices() {
        updateBestPrice("BTCUSDT");
        updateBestPrice("ETHUSDT");
        System.out.println("--- Đã cập nhật giá tốt nhất lúc: " + LocalDateTime.now());
    }

    private void updateBestPrice(String symbol) {
        try {
            BinanceTicker binance = restTemplate.getForObject(BINANCE_URL + symbol, BinanceTicker.class);
            
            HuobiResponse huobiRes = restTemplate.getForObject(HUOBI_URL, HuobiResponse.class);
            HuobiTicker huobi = huobiRes.getData().stream()
                    .filter(t -> t.getSymbol().equalsIgnoreCase(symbol))
                    .findFirst().orElse(null);

            if (binance != null && huobi != null) {
                BigDecimal bPriceBid = new BigDecimal(binance.getBidPrice());
                BigDecimal bPriceAsk = new BigDecimal(binance.getAskPrice());
                
                BigDecimal bestAsk = bPriceAsk.min(huobi.getAsk());
                BigDecimal bestBid = bPriceBid.max(huobi.getBid());

                BestPrice bestPrice = new BestPrice();
                bestPrice.setSymbol(symbol);
                bestPrice.setBestAsk(bestAsk);
                bestPrice.setBestBid(bestBid);
                bestPrice.setUpdatedAt(LocalDateTime.now());
                
                priceRepository.save(bestPrice);
            }
        } catch (Exception e) {
            System.err.println("An error occur when fetching price for " + symbol + ": " + e.getMessage());
        }
    }
}

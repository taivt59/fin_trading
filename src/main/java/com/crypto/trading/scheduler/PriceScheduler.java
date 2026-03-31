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

    @Scheduled(fixedRate = 10000) // Chạy mỗi 10 giây
    public void fetchAndAggregatePrices() {
        updateBestPrice("BTCUSDT");
        updateBestPrice("ETHUSDT");
        System.out.println("--- Đã cập nhật giá tốt nhất lúc: " + LocalDateTime.now());
    }

    private void updateBestPrice(String symbol) {
        try {
            // 1. Lấy giá từ Binance
            BinanceTicker binance = restTemplate.getForObject(BINANCE_URL + symbol, BinanceTicker.class);
            
            // 2. Lấy giá từ Huobi (Huobi trả về list, cần filter đúng symbol)
            HuobiResponse huobiRes = restTemplate.getForObject(HUOBI_URL, HuobiResponse.class);
            HuobiTicker huobi = huobiRes.getData().stream()
                    .filter(t -> t.getSymbol().equalsIgnoreCase(symbol))
                    .findFirst().orElse(null);

            if (binance != null && huobi != null) {
                BigDecimal bPriceBid = new BigDecimal(binance.getBidPrice());
                BigDecimal bPriceAsk = new BigDecimal(binance.getAskPrice());
                
                // SO SÁNH ĐỂ TÌM GIÁ TỐT NHẤT (Aggregated Price)
                // - MUA (ASK): Chọn giá THẤP NHẤT giữa 2 sàn để tiết kiệm tiền cho User.
                // - BÁN (BID): Chọn giá CAO NHẤT giữa 2 sàn để User lời nhiều nhất.
                
                BigDecimal bestAsk = bPriceAsk.min(huobi.getAsk());
                BigDecimal bestBid = bPriceBid.max(huobi.getBid());

                // 3. Lưu vào Database
                BestPrice bestPrice = new BestPrice();
                bestPrice.setSymbol(symbol);
                bestPrice.setBestAsk(bestAsk);
                bestPrice.setBestBid(bestBid);
                bestPrice.setUpdatedAt(LocalDateTime.now());
                
                priceRepository.save(bestPrice);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy giá cho " + symbol + ": " + e.getMessage());
        }
    }
}

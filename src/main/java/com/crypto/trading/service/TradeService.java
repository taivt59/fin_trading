package com.crypto.trading.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crypto.trading.entity.BestPrice;
import com.crypto.trading.entity.TradeTransaction;
import com.crypto.trading.entity.Wallet;
import com.crypto.trading.repository.BestPriceRepository;
import com.crypto.trading.repository.TransactionRepository;
import com.crypto.trading.repository.WalletRepository;

import jakarta.transaction.Transactional;

@Service
public class TradeService {
    @Autowired
    private WalletRepository walletRepo;
    
    @Autowired 
    private BestPriceRepository priceRepo;
    
    @Autowired 
    private TransactionRepository transRepo;
@Transactional
    public String executeTrade(String symbol, String type, BigDecimal qty) {
        // 1. Lấy giá tốt nhất hiện tại từ Database (do Scheduler cập nhật)
        BestPrice currentPrice = priceRepo.findById(symbol)
                .orElseThrow(() -> new RuntimeException("Chưa có dữ liệu giá cho " + symbol));

        // Tách symbol để biết loại coin (VD: BTCUSDT -> BTC và USDT)
        String cryptoCurrency = symbol.replace("USDT", ""); 
        
        Wallet usdtWallet = walletRepo.findByCurrency("USDT")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ví USDT"));
        Wallet cryptoWallet = walletRepo.findByCurrency(cryptoCurrency)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ví " + cryptoCurrency));

        BigDecimal executionPrice;
        BigDecimal totalUsdtAmount;

        if ("BUY".equalsIgnoreCase(type)) {
            executionPrice = currentPrice.getBestAsk(); // Giá mua (Ask)
            totalUsdtAmount = executionPrice.multiply(qty);

            // Kiểm tra số dư USDT
            if (usdtWallet.getBalance().compareTo(totalUsdtAmount) < 0) {
                return "Giao dịch thất bại: Không đủ số dư USDT!";
            }

            // Thực hiện trừ USDT, cộng Crypto
            usdtWallet.setBalance(usdtWallet.getBalance().subtract(totalUsdtAmount));
            cryptoWallet.setBalance(cryptoWallet.getBalance().add(qty));

        } else if ("SELL".equalsIgnoreCase(type)) {
            executionPrice = currentPrice.getBestBid(); // Giá bán (Bid)
            
            // Kiểm tra số dư Crypto (BTC hoặc ETH)
            if (cryptoWallet.getBalance().compareTo(qty) < 0) {
                return "Giao dịch thất bại: Không đủ số dư " + cryptoCurrency + "!";
            }

            // Thực hiện cộng USDT, trừ Crypto
            totalUsdtAmount = executionPrice.multiply(qty);
            usdtWallet.setBalance(usdtWallet.getBalance().add(totalUsdtAmount));
            cryptoWallet.setBalance(cryptoWallet.getBalance().subtract(qty));

        } else {
            return "Loại giao dịch không hợp lệ!";
        }

        // 2. Lưu cập nhật ví vào DB
        walletRepo.save(usdtWallet);
        walletRepo.save(cryptoWallet);

        // 3. Lưu lịch sử giao dịch (Transaction History)
        TradeTransaction transaction = new TradeTransaction();
        transaction.setSymbol(symbol);
        transaction.setType(type.toUpperCase());
        transaction.setQuantity(qty);
        transaction.setPrice(executionPrice);
        transaction.setTimestamp(LocalDateTime.now());
        transRepo.save(transaction);

        return String.format("Giao dịch %s %s thành công tại mức giá %s", type, symbol, executionPrice);
    }
}
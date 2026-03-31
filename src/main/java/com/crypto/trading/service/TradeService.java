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
        BestPrice currentPrice = priceRepo.findById(symbol)
                .orElseThrow(() -> new RuntimeException("No price data available yet " + symbol));

        String cryptoCurrency = symbol.replace("USDT", ""); 
        
        Wallet usdtWallet = walletRepo.findByCurrency("USDT")
                .orElseThrow(() -> new RuntimeException("No USDT wallet found"));
        Wallet cryptoWallet = walletRepo.findByCurrency(cryptoCurrency)
                .orElseThrow(() -> new RuntimeException("Wallet not found " + cryptoCurrency));

        BigDecimal executionPrice;
        BigDecimal totalUsdtAmount;

        if ("BUY".equalsIgnoreCase(type)) {
            executionPrice = currentPrice.getBestAsk();
            totalUsdtAmount = executionPrice.multiply(qty);

            if (usdtWallet.getBalance().compareTo(totalUsdtAmount) < 0) {
                return "Transaction failed: Insufficient USDT balance!";
            }

            usdtWallet.setBalance(usdtWallet.getBalance().subtract(totalUsdtAmount));
            cryptoWallet.setBalance(cryptoWallet.getBalance().add(qty));

        } else if ("SELL".equalsIgnoreCase(type)) {
            executionPrice = currentPrice.getBestBid();
            
            if (cryptoWallet.getBalance().compareTo(qty) < 0) {
                return "Transaction failed: Insufficient balance " + cryptoCurrency + "!";
            }

            totalUsdtAmount = executionPrice.multiply(qty);
            usdtWallet.setBalance(usdtWallet.getBalance().add(totalUsdtAmount));
            cryptoWallet.setBalance(cryptoWallet.getBalance().subtract(qty));

        } else {
            return "Invalid transaction type!";
        }

        walletRepo.save(usdtWallet);
        walletRepo.save(cryptoWallet);

        TradeTransaction transaction = new TradeTransaction();
        transaction.setSymbol(symbol);
        transaction.setType(type.toUpperCase());
        transaction.setQuantity(qty);
        transaction.setPrice(executionPrice);
        transaction.setTimestamp(LocalDateTime.now());
        transRepo.save(transaction);

        return String.format("Transaction %s %s successful at price %s", type, symbol, executionPrice);
    }
}
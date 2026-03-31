package com.crypto.trading.controller;

import com.crypto.trading.entity.BestPrice;
import com.crypto.trading.entity.TradeTransaction;
import com.crypto.trading.entity.Wallet;
import com.crypto.trading.repository.BestPriceRepository;
import com.crypto.trading.repository.TransactionRepository;
import com.crypto.trading.repository.WalletRepository;
import com.crypto.trading.service.TradeService;
import com.crypto.trading.dto.TradeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CryptoController {

    @Autowired 
    private TradeService tradeService;

    @Autowired 
    private BestPriceRepository priceRepo;

    @Autowired 
    private WalletRepository walletRepo;

    @Autowired 
    private TransactionRepository transRepo;

    @GetMapping("/prices")
    public List<BestPrice> getLatestPrices() {
        return priceRepo.findAll(); 
    }

    @PostMapping("/trade")
    public String trade(@RequestBody TradeRequest request) {
        return tradeService.executeTrade(
            request.getSymbol(), 
            request.getType(), 
            request.getQuantity()
        );
    }

    @GetMapping("/wallet")
    public List<Wallet> getWalletBalance() {
        return walletRepo.findAll();
    }

    @GetMapping("/history")
    public List<TradeTransaction> getTradeHistory() {
        return transRepo.findAll();
    }
}

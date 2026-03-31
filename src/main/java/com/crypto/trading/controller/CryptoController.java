package com.crypto.trading.controller;

import com.crypto.trading.dto.ApiResponse;
import com.crypto.trading.dto.TradeRequest;
import com.crypto.trading.entity.BestPrice;
import com.crypto.trading.entity.TradeTransaction;
import com.crypto.trading.entity.Wallet;
import com.crypto.trading.repository.BestPriceRepository;
import com.crypto.trading.repository.TransactionRepository;
import com.crypto.trading.repository.WalletRepository;
import com.crypto.trading.service.TradeService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CryptoController {

    private final TradeService tradeService;
    private final BestPriceRepository priceRepo;
    private final WalletRepository walletRepo;
    private final TransactionRepository transRepo;

    // =========================
    // 1. GET ALL PRICES
    // =========================
    @GetMapping("/prices")
    public ResponseEntity<ApiResponse<List<BestPrice>>> getLatestPrices() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Get all prices success", priceRepo.findAll())
        );
    }

    // =========================
    // 2. GET PRICE BY SYMBOL
    // =========================
    @GetMapping("/prices/{symbol}")
    public ResponseEntity<?> getPriceBySymbol(@PathVariable String symbol) {
        return priceRepo.findBySymbol(symbol)
                .map(price -> ResponseEntity.ok(
                        new ApiResponse<>(true, "Get price success", price)
                ))
                .orElse(ResponseEntity.status(404).body(
                        new ApiResponse<>(false, "Symbol not found", null)
                ));
    }

    // =========================
    // 3. TRADE
    // =========================
    @PostMapping("/trades")
    public ResponseEntity<ApiResponse<?>> trade(@RequestBody TradeRequest request) {
        try {
            String result = tradeService.executeTrade(
                    request.getSymbol(),
                    request.getType(),
                    request.getQuantity()
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Trade executed successfully", result)
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }

    // =========================
    // 4. WALLET
    // =========================
    @GetMapping("/wallets")
    public ResponseEntity<ApiResponse<List<Wallet>>> getWalletBalance() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Get wallet success", walletRepo.findAll())
        );
    }

    // =========================
    // 5. TRADE HISTORY
    // =========================
    @GetMapping("/trades")
    public ResponseEntity<ApiResponse<List<TradeTransaction>>> getTradeHistory() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Get trade history success", transRepo.findAll())
        );
    }
}
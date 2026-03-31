package com.crypto.trading.repository;

import com.crypto.trading.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    // Tìm ví theo loại tiền (VD: tìm ví USDT để trừ tiền khi mua)
    Optional<Wallet> findByCurrency(String currency);
}
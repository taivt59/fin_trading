package com.crypto.trading.repository;

import com.crypto.trading.entity.TradeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TradeTransaction, Long> {
}
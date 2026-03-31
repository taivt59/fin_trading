package com.crypto.trading.repository;

import com.crypto.trading.entity.BestPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BestPriceRepository extends JpaRepository<BestPrice, String> {
}
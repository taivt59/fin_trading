package com.crypto.trading.repository;

import com.crypto.trading.entity.BestPrice;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BestPriceRepository extends JpaRepository<BestPrice, String> {
    Optional<BestPrice> findBySymbol(String symbol);

}
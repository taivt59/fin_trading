package com.crypto.trading.dto;

import java.util.List;

import com.crypto.trading.entity.HuobiTicker;

import lombok.Data;

@Data
public class HuobiResponse {
    private String status;
    private List<HuobiTicker> data;
}


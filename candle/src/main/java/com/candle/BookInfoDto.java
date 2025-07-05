package com.candle;

import com.candle.test.CandleApi;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@ToString
@Getter
public class BookInfoDto {
    private String symbol;
    private List<PriceQtyInfo> bids;
    private List<PriceQtyInfo> asks;

    @Builder
    @ToString
    @Getter
    public static class PriceQtyInfo {
        private double price;
        private double qty;
        private int qtyUsdt;
    }
}

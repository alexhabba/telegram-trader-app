package com.trade.bot.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BarDto {
    private String symbol;
    private BigDecimal volBuy;
    private BigDecimal volSell;
    private String open;
    private String close;
    private String low;
    private String high;
    private String createDate;
}

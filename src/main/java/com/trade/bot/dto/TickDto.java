package com.trade.bot.dto;

import lombok.Data;

@Data
public class TickDto {
    private String symbol;
    private String side;
    private String quantity;
    private String price;
    private String createDate;
    private String instrument;
}

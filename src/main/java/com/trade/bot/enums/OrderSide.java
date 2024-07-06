package com.trade.bot.enums;

public enum OrderSide {
    BUY("Buy"),
    SELL("Sell");

    private final String value;

    OrderSide(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

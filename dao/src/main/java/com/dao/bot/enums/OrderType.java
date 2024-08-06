package com.dao.bot.enums;

public enum OrderType {
    LIMIT("Limit"),
    MARKET("Market");

    private final String value;

    OrderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

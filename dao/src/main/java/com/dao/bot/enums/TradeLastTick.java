package com.dao.bot.enums;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public enum TradeLastTick {
    ZeroPlusTick,
    PlusTick,
    ZeroMinusTick,
    MinusTick
}

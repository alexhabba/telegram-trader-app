package com.strategy.bot.startegy.test;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WrapperBalance {
    private BigDecimal balance;
}

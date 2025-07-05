package com.strategy.bot.startegy;

import com.dao.bot.entity.Bar;

import java.time.LocalDateTime;

public interface StrategyExecutor {
    void execute(Bar lastBar, LocalDateTime lastDateTime);
}

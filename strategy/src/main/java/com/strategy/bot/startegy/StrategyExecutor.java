package com.strategy.bot.startegy;

import com.dao.bot.entity.Bar;

public interface StrategyExecutor {
    void execute(Bar lastBar);
}

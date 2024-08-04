package com.trade.bot.startegy;

import com.trade.bot.entity.Bar;

public interface StrategyExecutor {
    void execute();
    void execute(Bar lastBar);
}

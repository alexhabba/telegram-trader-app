package com.strategy.bot.job;

import com.dao.bot.service.BarDaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SomeJob {

    private final BarDaoService barDaoService;
}

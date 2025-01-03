package com.strategy.bot.job;

import com.dao.bot.service.BarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SomeJob {

    private final BarService barService;
}

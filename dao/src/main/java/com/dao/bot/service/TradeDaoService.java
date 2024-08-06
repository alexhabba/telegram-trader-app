package com.dao.bot.service;

import com.dao.bot.repository.TickRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeDaoService {

    private final TickRepository tickRepository;

}

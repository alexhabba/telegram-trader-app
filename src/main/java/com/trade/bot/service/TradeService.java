package com.trade.bot.service;

import com.trade.bot.dto.TickDto;
import com.trade.bot.entity.Tick;
import com.trade.bot.mapper.TickMapper;
import com.trade.bot.repository.TickRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TickMapper tickMapper;
    private final TickRepository tickRepository;

    public List<TickDto> getTicks(double size, String exchange, LocalDateTime createDate) {
        List<Tick> ticks = tickRepository.findTickByCreateDateAfter(createDate);
        ticks = ticks.stream()
                .filter(tick -> Double.parseDouble(tick.getQuantity()) >= size)
                .filter(tick -> tick.getExchange().equals(exchange))
                .sorted(Comparator.comparing(Tick::getCreateDate))
                .collect(Collectors.toList());
        return tickMapper.toDto(ticks);
    }

    public List<TickDto> getTicksCreateDateBetween(double size, String exchange, LocalDateTime start, LocalDateTime end) {
        List<Tick> ticks = tickRepository.findTickByCreateDateBetween(start, end);
        ticks = ticks.stream()
                .filter(tick -> Double.parseDouble(tick.getQuantity()) >= size)
                .filter(tick -> tick.getExchange().equals(exchange))
                .sorted(Comparator.comparing(Tick::getCreateDate))
                .collect(Collectors.toList());
        return tickMapper.toDto(ticks);
    }
}

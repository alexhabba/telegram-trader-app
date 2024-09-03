package com.trade.bot.service;

import com.trade.bot.dto.BarDto;
import com.trade.bot.entity.Bar;
import com.trade.bot.mapper.BarMapper;
import com.trade.bot.repository.BarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BarService {

    @Value("${hour}")
    private int hour;
    private final BarMapper barMapper;
    private final BarRepository barRepository;

    public List<BarDto> getBars() {
        LocalDateTime end = LocalDateTime.now().minusHours(hour);
        return barMapper.toDto(barRepository.findBarByCreateDateBetween(end.minusMinutes(5), end));
    }


    public List<BigDecimal> getBarsAverage() {
        LocalDateTime end = LocalDateTime.now().minusHours(hour);
        return barMapper.toDto(barRepository.findBarByCreateDateBetween(end.minusMinutes(50), end)).stream()
                .map(BarDto::getClose)
                .map(BigDecimal::new)
                .collect(Collectors.toList());
    }

    public List<BarDto> getBarss() {
        LocalDateTime end = LocalDateTime.now().minusHours(hour);
        return barMapper.toDto(barRepository.findBarByCreateDateBetween(end.minusMinutes(50), end));
    }

    public List<BarDto> getBarsCreateDateBetween(LocalDateTime start, LocalDateTime end) {
        return barMapper.toDto(barRepository.findBarByCreateDateBetween(start, end));
    }

    public List<Bar> findLastBar(int count) {
      return barRepository.findLastBar(count, "TON");
    }

    public List<Bar> findAll() {
        return barRepository.findAll();
    }

    public void deleteAll() {
        barRepository.deleteAll();
    }
}

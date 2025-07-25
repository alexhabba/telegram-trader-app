package com.dao.bot.service;

import com.dao.bot.entity.Bar;
import com.dao.bot.enums.Symbol;
import com.dao.bot.repository.BarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BarService {

    private final BarRepository barRepository;

    public Bar findLastBarBySymbol(String symbol) {
        return barRepository.findLastBarBySymbol(symbol).orElseThrow(EntityNotFoundException::new);
    }

    public List<Bar> findLastBarBySymbolAndByCount(String symbol, LocalDateTime currentDate, int count) {
        return barRepository.findLastBarBySymbolAndByCount(symbol, currentDate, count);
    }

    public void saveAll(List<Bar> bars) {
        barRepository.saveAll(bars);
    }

    public double getAvg(String symbol, LocalDateTime dateTime) {
        return barRepository.getAvg(symbol, dateTime.minusMinutes(113), dateTime);
    }

    public List<Bar> findAll() {
        return barRepository.findAll();
    }

    public List<Bar> findAllBySymbol(Symbol symbol) {
        return barRepository.findAllBySymbol(symbol);
    }

    public void deleteAll() {
        barRepository.deleteAll();
    }
}

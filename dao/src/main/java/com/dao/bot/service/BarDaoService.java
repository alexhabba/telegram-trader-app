package com.dao.bot.service;

import com.dao.bot.entity.Bar;
import com.dao.bot.enums.Symbol;
import com.dao.bot.repository.BarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BarDaoService {

    private final BarRepository barRepository;

    public List<Bar> findLastBarBySymbol(Symbol symbol) {
      return barRepository.findLastBarBySymbol(symbol.name());
    }

    public List<Bar> findAll() {
        return barRepository.findAll();
    }

    public List<Bar> findAllBySymbol(Symbol symbol) {
        return barRepository.findAllBySymbol(symbol.name());
    }

    public void deleteAll() {
        barRepository.deleteAll();
    }
}

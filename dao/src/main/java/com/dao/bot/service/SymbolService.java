package com.dao.bot.service;

import com.dao.bot.entity.Symbol;
import com.dao.bot.repository.SymbolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SymbolService {

    private final SymbolRepository symbolRepository;

    public List<Symbol> getAllSymbol() {
        return symbolRepository.findAll();
    }
}

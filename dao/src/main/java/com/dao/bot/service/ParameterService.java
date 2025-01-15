package com.dao.bot.service;

import com.dao.bot.entity.Parameter;
import com.dao.bot.enums.Symbol;
import com.dao.bot.repository.ParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class ParameterService {

    private final ParameterRepository parameterRepository;

    public Parameter getParameter(Symbol symbol, int strategy) {
        return parameterRepository.findParameterBySymbolAndStrategy(symbol, strategy).orElseThrow(EntityNotFoundException::new);
    }
}

package com.dao.bot.service;

import com.dao.bot.entity.Parameter;
import com.dao.bot.enums.Symbol;
import com.dao.bot.repository.ParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParameterService {

    private final ParameterRepository parameterRepository;

    public Parameter getParameter(Symbol symbol, int strategy) {
        return parameterRepository.findParameterBySymbolAndStrategy(symbol, strategy).orElseThrow(EntityNotFoundException::new);
    }

    @Cacheable(value = "parameters")
    public List<Parameter> getParameters(Symbol symbol, List<Integer> strategies) {
        return parameterRepository.findParameterBySymbolAndStrategies(symbol.name(), strategies);
    }
}

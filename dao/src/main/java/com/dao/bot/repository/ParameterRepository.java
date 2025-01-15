package com.dao.bot.repository;

import com.dao.bot.entity.Parameter;
import com.dao.bot.enums.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParameterRepository extends JpaRepository<Parameter, UUID> {
    Optional<Parameter> findParameterBySymbolAndStrategy(Symbol symbol, int strategy);
}

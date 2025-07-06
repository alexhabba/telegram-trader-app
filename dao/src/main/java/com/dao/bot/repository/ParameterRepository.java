package com.dao.bot.repository;

import com.dao.bot.entity.Parameter;
import com.dao.bot.enums.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParameterRepository extends JpaRepository<Parameter, UUID> {
    Optional<Parameter> findParameterBySymbolAndStrategy(Symbol symbol, int strategy);

    @Query(value = "select * from  parameter p where p.symbol = :symbol and p.strategy in (:strategies)", nativeQuery = true)
    List<Parameter> findParameterBySymbolAndStrategies(String symbol, List<Integer> strategies);
}

package com.trade.bot.repository;

import com.trade.bot.entity.Tick;
import com.trade.bot.enums.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TickRepository extends JpaRepository<Tick, Long> {

    List<Tick> findTickByCreateDateAfter(LocalDateTime createDate);

    List<Tick> findTickBySymbolAndCreateDateBetween(Symbol symbol, LocalDateTime createDateStart, LocalDateTime createDateEnd);

    @Query(value = "SELECT *\n" +
            "FROM tick\n" +
            "WHERE exchange = 'binance' AND symbol = :symbol \n" +
            "ORDER BY create_date \n" +
            "LIMIT 1;", nativeQuery = true)
    Optional<Tick> findFirstTick(String symbol);
}

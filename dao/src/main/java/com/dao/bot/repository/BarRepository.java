package com.dao.bot.repository;

import com.dao.bot.entity.Bar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BarRepository extends JpaRepository<Bar, LocalDateTime> {

    @Query(value = "WITH latest_dates AS (\n" +
            "    SELECT symbol, MAX(create_date) AS max_date\n" +
            "    FROM bot.bar\n" +
            "    WHERE symbol IN :symbols\n" +
            "    GROUP BY symbol\n" +
            ")\n" +
            "SELECT b.*\n" +
            "FROM bot.bar b\n" +
            "JOIN latest_dates ld ON b.symbol = ld.symbol AND b.create_date = ld.max_date\n" +
            "ORDER BY b.symbol;", nativeQuery = true)
    List<Bar> findLastBarBySymbol(List<String> symbols);

    @Query(value = "SELECT *\n" +
            "FROM bar where symbol = :symbol " +
            "ORDER BY create_date DESC\n" +
            "LIMIT 1", nativeQuery = true)
    List<Bar> findLastBarBySymbol(String symbol);

    List<Bar> findAllBySymbol(String symbol);

    List<Bar> findBarByCreateDateBetween(LocalDateTime createDateStart, LocalDateTime createDateEnd);
}

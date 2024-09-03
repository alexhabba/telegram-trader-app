package com.trade.bot.repository;

import com.trade.bot.entity.Bar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BarRepository extends JpaRepository<Bar, LocalDateTime> {

    @Query(value = "SELECT *\n" +
            "FROM bar b WHERE b.symbol = :symbol " +
            "ORDER BY create_date DESC\n" +
            "LIMIT :count", nativeQuery = true)
    List<Bar> findLastBar(int count, String symbol);

    List<Bar> findBarByCreateDateBetween(LocalDateTime createDateStart, LocalDateTime createDateEnd);

}

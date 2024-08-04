package com.trade.bot.repository;

import com.trade.bot.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface DealRepository extends JpaRepository<Deal, LocalDateTime> {

    @Query(value = "SELECT *\n" +
            "FROM deal\n" +
            "ORDER BY open_date DESC\n" +
            "LIMIT :count", nativeQuery = true)
    List<Deal> findLastDeal(int count);

//    List<Bar> findBarByCreateDateBetween(LocalDateTime createDateStart, LocalDateTime createDateEnd);

}

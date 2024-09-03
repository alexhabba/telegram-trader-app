package com.dao.bot.repository;

import com.dao.bot.entity.Bar;
import com.dao.bot.entity.Deal;
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

    @Query(value = "SELECT *\n" +
            "FROM deal d where d.strategy = :strategy and d.status = 'COMPLETED' " +
            "ORDER BY open_date DESC\n" +
            "LIMIT :count", nativeQuery = true)
    List<Deal> findLastDealStrategy(int count, String strategy);

//    List<Bar> findBarByCreateDateBetween(LocalDateTime createDateStart, LocalDateTime createDateEnd);

}

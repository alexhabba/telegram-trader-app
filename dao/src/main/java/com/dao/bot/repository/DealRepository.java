package com.dao.bot.repository;

import com.dao.bot.entity.Deal;
import com.dao.bot.enums.Status;
import com.dao.bot.enums.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DealRepository extends JpaRepository<Deal, UUID> {

    @Query(value = "SELECT *\n" +
            "FROM deal\n" +
            "ORDER BY open_date DESC\n" +
            "LIMIT :count", nativeQuery = true)
    List<Deal> findLastDeal(int count);

    @Query(value = "SELECT *\n" +
            "FROM deal d where d.strategy = :strategy and d.status not in ('CANCEL') and d.symbol = :symbol " +
            "ORDER BY open_date DESC\n" +
            "LIMIT :count", nativeQuery = true)
    List<Deal> findLastDealStrategy(int count, String strategy, String symbol);

    List<Deal> findDealByStatusAndSymbol(Status status, Symbol symbol);

//    List<Bar> findBarByCreateDateBetween(LocalDateTime createDateStart, LocalDateTime createDateEnd);

}

package com.trade.bot.repository;

import com.trade.bot.entity.Tick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TickRepository extends JpaRepository<Tick, Long> {

    List<Tick> findTickByCreateDateAfter(LocalDateTime createDate);

    List<Tick> findTickByCreateDateBetween(LocalDateTime createDateStart, LocalDateTime createDateEnd);
}

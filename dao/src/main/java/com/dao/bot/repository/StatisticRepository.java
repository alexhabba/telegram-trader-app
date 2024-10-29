package com.dao.bot.repository;

import com.dao.bot.entity.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StatisticRepository extends JpaRepository<Statistic, UUID> {
}

package com.trade.bot.repository;

import com.trade.bot.entity.Fractal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface FractalRepository extends JpaRepository<Fractal, LocalDateTime> {

}

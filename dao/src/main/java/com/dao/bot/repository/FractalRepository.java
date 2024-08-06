package com.dao.bot.repository;

import com.dao.bot.entity.Fractal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

public interface FractalRepository extends JpaRepository<Fractal, LocalDateTime> {

}

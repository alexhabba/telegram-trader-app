package com.dao.bot.repository;

import com.dao.bot.entity.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SymbolRepository extends JpaRepository<Symbol, UUID> {
}

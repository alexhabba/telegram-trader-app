package com.dao.bot.repository;

import com.dao.bot.entity.Order;
import com.dao.bot.enums.Status;
import com.dao.bot.enums.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findBySymbol(Symbol symbol);

    Optional<Order> findByStatus(Status status);

}

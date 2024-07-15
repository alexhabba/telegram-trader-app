package com.trade.bot.repository;

import com.trade.bot.entity.Order;
import com.trade.bot.entity.TelegramUser;
import com.trade.bot.enums.Role;
import com.trade.bot.enums.Status;
import com.trade.bot.enums.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findBySymbol(Symbol symbol);

    Optional<Order> findByStatus(Status status);

}

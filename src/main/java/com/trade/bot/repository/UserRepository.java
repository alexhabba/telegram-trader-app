package com.trade.bot.repository;

import com.trade.bot.enums.Role;
import com.trade.bot.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<TelegramUser, Long> {

    List<TelegramUser> findAllByRole(Role role);
}

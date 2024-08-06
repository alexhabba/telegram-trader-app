package com.dao.bot.repository;

import com.dao.bot.entity.TelegramUser;
import com.dao.bot.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface UserRepository extends JpaRepository<TelegramUser, Long> {

    List<TelegramUser> findAllByRole(Role role);
}

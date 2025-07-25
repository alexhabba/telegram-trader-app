package com.dao.bot.repository;

import com.dao.bot.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findAccountByIsActiveTrue();
    Optional<Account> findAccountByNameAndIsActiveTrue(String name);
}

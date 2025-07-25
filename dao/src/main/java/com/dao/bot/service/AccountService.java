package com.dao.bot.service;

import com.dao.bot.entity.Account;
import com.dao.bot.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Cacheable(value = "accounts")
    public List<Account> findAccountByIsActiveTrue() {
        return accountRepository.findAccountByIsActiveTrue();
    }

    @Cacheable(value = "accounts")
    public Optional<Account> findAccountByIsActiveTrue(String name) {
        return accountRepository.findAccountByNameAndIsActiveTrue(name);
    }
}

package com.dao.bot.service;

import com.dao.bot.entity.Account;
import com.dao.bot.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Cacheable(value = "accounts")
    public List<Account> findAccountByIsActiveTrue() {
        return accountRepository.findAccountByIsActiveTrue();
    }
}

package com.strategy.bot.service;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.account.AccountType;
import com.bybit.api.client.domain.account.request.AccountDataRequest;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strategy.bot.dto.WalletBalanceDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BybitBalanceService {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public BigDecimal getBalance(String key, String secret) {
        var client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN, true).newAccountRestClient();
        AccountDataRequest request = AccountDataRequest.builder().accountType(AccountType.UNIFIED).coin("USDT").build();
        Object walletBalance = client.getWalletBalance(request);
        String string = objectMapper.writeValueAsString(walletBalance);
        WalletBalanceDto wallet = objectMapper.readValue(string, WalletBalanceDto.class);
        return wallet.getResult().getList().get(0).getTotalEquity();
    }
}
package com.strategy.bot.service;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.position.request.PositionDataRequest;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strategy.bot.dto.ResponsePosition;
import com.strategy.bot.utils.PositionUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BybitPositionService {

    private final ObjectMapper objectMapper;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        System.out.println("");
    }

    @SneakyThrows
    public ResponsePosition getPosition(String key, String secret) {
        var client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN, true).newPositionRestClient();
        Object response = client.getPositionInfo(PositionDataRequest.builder().category(CategoryType.LINEAR).symbol("WLDUSDT").build());
        return objectMapper.readValue(objectMapper.writeValueAsString(response), ResponsePosition.class);
    }

    @SneakyThrows
    public void setSlTp(String key, String secret, BigDecimal sl, BigDecimal tp) {
        PositionUtils.sentTpSl(key, secret, sl, tp);
    }
}

package com.strategy.bot.service;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.position.request.PositionDataRequest;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.dao.bot.enums.Symbol;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strategy.bot.dto.ResponsePosition;
import com.strategy.bot.utils.PositionUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BybitPositionService {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public ResponsePosition getPosition(String key, String secret, Symbol symbol) {
        var client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN, true).newPositionRestClient();
        Object response = client.getPositionInfo(PositionDataRequest.builder().category(CategoryType.LINEAR).symbol(symbol.name() + "USDT").build());
        return objectMapper.readValue(objectMapper.writeValueAsString(response), ResponsePosition.class);
    }

    @SneakyThrows
    public void setSlTp(String key, String secret, BigDecimal sl, BigDecimal tp) {
        PositionUtils.sentTpSl(key, secret, sl, tp, Symbol.SOL);
    }

    public static void main(String[] args) {
        BybitPositionService bybitPositionService = new BybitPositionService(new ObjectMapper());
        ResponsePosition position = bybitPositionService.getPosition("mXtga6i1kKM7E6QxZd", "xdockA1PaahdKwGecn18VgngE2ddXwhF5z0e", Symbol.SOL);
        System.out.println(position);
    }
}

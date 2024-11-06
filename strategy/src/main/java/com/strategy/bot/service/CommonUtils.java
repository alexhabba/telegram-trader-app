package com.strategy.bot.service;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.trade.request.TradeOrderRequest;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strategy.bot.dto.BybitLimitOrderResponse;
import lombok.SneakyThrows;

import java.util.UUID;

public class CommonUtils {

    /**
     * Проверка
     *
     * @return TRUE - если позиция перешла в статус Filled - то есть перешла из лимитной заявки в позицию
     */
    @SneakyThrows
    public static boolean isOpenPositionFromLimitOrder(String key, String secret, UUID orderId, String symbol) {
        var client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN).newTradeRestClient();

        Object resul = client.getOpenOrders(TradeOrderRequest.builder().category(CategoryType.LINEAR).symbol(symbol).orderId(orderId.toString()).build());

        ObjectMapper objectMapper = new ObjectMapper();
        BybitLimitOrderResponse bybitLimitOrderResponse = objectMapper.readValue(objectMapper.writeValueAsString(resul), BybitLimitOrderResponse.class);
        String orderStatus = bybitLimitOrderResponse.getResult().getList().get(0).getOrderStatus();
        return "Filled".equals(orderStatus);
    }
}

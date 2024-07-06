package com.trade.bot.service;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.exception.BybitApiException;
import com.bybit.api.client.restApi.BybitApiCallback;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.trade.bot.enums.OrderSide;
import com.trade.bot.enums.OrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Value("${key}")
    private String KEY;
    @Value("${secret}")
    private String SECRET;

    public void openOrder(String symbol, BigDecimal price, BigDecimal qty,
                           OrderSide side, OrderType orderType, UUID orderLinkId, BybitApiCallback<Object> callback) {
        try {
            var client = BybitApiClientFactory.newInstance(KEY, SECRET, BybitApiConfig.MAINNET_DOMAIN, true).newAsyncTradeRestClient();
            Map<String, Object> order = Map.of(
                    "category", "linear",
                    "symbol", symbol + "USDT",
                    "side", side.getValue(),
                    "orderType", orderType.getValue(),
                    "qty", qty.toString(),
                    "price", price.toString(),
                    "orderLinkId", orderLinkId.toString()
            );
            client.createOrder(order, callback);
        } catch (BybitApiException e) {
            // Обработка ошибок
            System.err.println("Ошибка: " + e.getMessage());
        }

    }
}

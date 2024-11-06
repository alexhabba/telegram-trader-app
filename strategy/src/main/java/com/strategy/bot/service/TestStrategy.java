package com.strategy.bot.service;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.exception.BybitApiException;
import com.bybit.api.client.restApi.BybitApiCallback;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.dao.bot.enums.OrderType;
import com.dao.bot.enums.Side;
import com.dao.bot.enums.Symbol;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestStrategy {

    public void openOrder(String key, String secret, Symbol symbol, String st, String tp, String qty,
                          Side side, OrderType orderType, UUID orderLinkId, int hedgeMode, BybitApiCallback<Object> callback) {
        try {
            var client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN, true).newAsyncTradeRestClient();
            Map<String, Object> order = new HashMap<>();
                    order.put("category", "linear");
                    order.put("symbol", symbol.name() + "USDT");
                    order.put("side", side);
                    order.put("orderType", orderType.getValue());
                    order.put("qty", qty);
                    order.put("orderLinkId", orderLinkId.toString());
                    order.put("stopLoss", st);
                    order.put("takeProfit", tp);
                    order.put("slOrderType", "Market");
                    order.put("tpslMode", "Full");
                    order.put("positionIdx", hedgeMode);

            client.createOrder(order, callback);
        } catch (BybitApiException e) {
            // Обработка ошибок
            System.err.println("Ошибка: " + e.getMessage());
        }

    }

    @SneakyThrows
    public static void main(String[] args) {
//        closeOpenLimitOrder("x29QaRh6pSDzmTLUAO", "ZGDBtgo5GX1KBoLl1RTjsJk0CWHeIpwgdSxy", true);
        TestStrategy service = new TestStrategy();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> {
            while (true) {
//                "10", Pair.of("6KHHWQ26pEBvLGTvNq", "ADD12KPrgwmMBewxeWaWj1dGbLvyooJtLZYB")

                service.openOrder("6KHHWQ26pEBvLGTvNq",
                        "ADD12KPrgwmMBewxeWaWj1dGbLvyooJtLZYB",
                        Symbol.WLD,
                        "2.480",
                        "2",
                        "3",
                        Side.Sell,
                        OrderType.MARKET,
                        UUID.randomUUID(),
                        2,
                        System.out::println);
                Thread.sleep(300000);
            }
        });
//        executorService.submit(() -> {
//            while (true) {
//                service.openOrder("XoX4nqAL5ZZxqr3r0j",
//                        "TavNLVR6Q6nkbOvGye3JeeEvLNksptTwrIxF",
//                        Symbol.WLD,
//                        "2",
//                        "2.5",
//                        "3",
//                        Side.Buy,
//                        OrderType.MARKET,
//                        UUID.randomUUID(),
//                        1,
//                        System.out::println);
//                Thread.sleep(600000);
//            }
//        });
    }
}

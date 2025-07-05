package com.strategy.bot.service;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.exception.BybitApiException;
import com.bybit.api.client.restApi.BybitApiCallback;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.dao.bot.enums.OrderType;
import com.dao.bot.enums.Side;
import com.dao.bot.enums.Symbol;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestStrategy {

    @SneakyThrows
    public UUID openOrder(String key, String secret, Symbol symbol, String st, String tp, String qty,
                          Side side, OrderType orderType, UUID orderLinkId, int hedgeMode, BybitApiCallback<Object> callback) {
        try {
            var client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN, true).newTradeRestClient();
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
//                    order.put("positionIdx", hedgeMode);

            Object response = client.createOrder(order);

            Object orderId = ((LinkedHashMap<?, ?>) ((LinkedHashMap<?, ?>) response).get("result")).get("orderId");
            UUID uuid = UUID.fromString((String) orderId);
            return UUID.fromString((String) orderId);
        } catch (BybitApiException e) {
            // Обработка ошибок
            System.err.println("Ошибка: " + e.getMessage());
            throw e;
        }

    }

    @SneakyThrows
    public static void main(String[] args) {
//        closeOpenLimitOrder("x29QaRh6pSDzmTLUAO", "ZGDBtgo5GX1KBoLl1RTjsJk0CWHeIpwgdSxy", true);
        TestStrategy service = new TestStrategy();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> {
            while (true) {
                service.openOrder(
                        "6CKgANrPFtih7TAAI4",
                        "lu5WwteC0SOcT5IDgxYC9gMEFCuFONUIbaOR",
                        Symbol.SOL,
                        "150",
                        "140",
                        "0.4",
                        Side.Sell,
                        OrderType.MARKET,
                        UUID.randomUUID(),
                        2,
                        System.out::println);
                Thread.sleep(3000000);
            }
        });
//        Unrecognized field "retCode" (class com.strategy.bot.dto.ResponseDto), not marked as ignorable
//        {retCode=0, retMsg=OK, result={orderId=8e5cd745-a01f-4bbf-87dc-4c595a02b2d6, orderLinkId=e1117ad5-bdaf-4610-886f-d882afe95408}, retExtInfo={}, time=1732446468730}

//        executorService.submit(() -> {
//            while (true) {
//                service.openOrder("XoX4nqAL5ZZxqr3r0j",
//                        "TavNLVR6Q6nkbOvGye3JeeEvLNksptTwrIxF",
//                        Symbol.SOL,
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

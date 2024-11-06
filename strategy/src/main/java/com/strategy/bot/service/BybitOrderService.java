package com.strategy.bot.service;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.trade.request.TradeOrderRequest;
import com.bybit.api.client.exception.BybitApiException;
import com.bybit.api.client.restApi.BybitApiCallback;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.dao.bot.enums.OrderType;
import com.dao.bot.enums.Side;
import com.dao.bot.enums.Symbol;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strategy.bot.dto.BybitLimitOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BybitOrderService {

    private final ObjectMapper objectMapper;

    public void openOrder(String key, String secret, Symbol symbol, String st, String tp, String qty,
                          Side side, OrderType orderType, UUID orderLinkId, BybitApiCallback<Object> callback) {
        try {
            var client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN, true).newAsyncTradeRestClient();
            Map<String, Object> order = Map.of(
                    "category", "linear",
                    "symbol", symbol.name() + "USDT",
                    "side", side,
                    "orderType", orderType.getValue(),
                    "qty", qty,
//                    "price", tvh,
                    "orderLinkId", orderLinkId.toString(),
                    "stopLoss", st,
                    "takeProfit", tp,
                    "slOrderType", "Market",
                    "tpslMode", "Full"
            );

            client.createOrder(order, callback);
        } catch (BybitApiException e) {
            // Обработка ошибок
            System.err.println("Ошибка: " + e.getMessage());
        }

    }

    public void openLimitOrder(String key, String secret, Symbol symbol, String tvh, String sl, String qty,
                               Side side, OrderType orderType, UUID orderLinkId, BybitApiCallback<Object> callback) {
        try {
            var client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN, true).newAsyncTradeRestClient();
            Map<String, Object> order = Map.of(
                    "category", "linear",
                    "symbol", symbol.name() + "USDT",
                    "side", side,
                    "orderType", orderType.getValue(),
                    "qty", qty,
                    "price", tvh,
                    "stopLoss", sl,
                    "orderLinkId", orderLinkId.toString(),
                    "slOrderType", "Market",
                    "tpslMode", "Full"
            );

            client.createOrder(order, callback);
        } catch (BybitApiException e) {
            // Обработка ошибок
            System.err.println("Ошибка: " + e.getMessage());
        }

    }

    private String getQty(String tvhh, String stopp) {
//        BigDecimal percent = BigDecimal.valueOf(13);
//        BigDecimal percent100 = BigDecimal.valueOf(100);

//        BigDecimal capital = getBalance("", "");

//        BigDecimal tvh = BigDecimal.valueOf(Double.parseDouble(tvhh));
//        BigDecimal stop = BigDecimal.valueOf(Double.parseDouble(stopp));
//
//        BigDecimal subtract = tvh.subtract(stop);
//        double countContract = Math.abs(capital.multiply(percent).divide(percent100, 5, CEILING).divide(subtract, 5, CEILING).doubleValue());
//        System.out.println(countContract);
//        return String.valueOf(countContract > 1 ? (int)countContract : countContract);
        return null;
    }

    @SneakyThrows
    public BybitLimitOrderResponse getOpenLimitOrder(String key, String secret) {
        try {
            var client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN).newTradeRestClient();
            var openLinearOrdersResult = client.getOpenOrders(TradeOrderRequest.builder().category(CategoryType.LINEAR).symbol("WLDUSDT").build());
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(objectMapper.writeValueAsString(openLinearOrdersResult), BybitLimitOrderResponse.class);
        } catch (BybitApiException e) {
            // Обработка ошибок
            System.err.println("Ошибка: " + e.getMessage());
        }
        return null;
    }

    @SneakyThrows
    public void closeOpenLimitOrder(String key, String secret) {
        try {
//            BybitLimitOrderResponse openLimitOrder = getOpenLimitOrder(key, secret);
            BybitLimitOrderResponse openLimitOrder = getOpenLimitOrder("x29QaRh6pSDzmTLUAO", "ZGDBtgo5GX1KBoLl1RTjsJk0CWHeIpwgdSxy");
            if (openLimitOrder.getResult().getList().isEmpty()) {
                return;
            }
            String orderId = openLimitOrder.getResult().getList().get(0).getOrderId();
            var client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN).newTradeRestClient();


            var result = client.cancelOrder(TradeOrderRequest.builder().category(CategoryType.LINEAR).symbol("WLDUSDT").orderId(orderId).build());
            System.out.println(result);
        } catch (BybitApiException e) {
            // Обработка ошибок
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    public static void closeOpenLimitOrder(String key, String secret, boolean t) {
        try {
            BybitLimitOrderResponse openLimitOrder = getOpenLimitOrderr(key, secret);
//            BybitLimitOrderResponse openLimitOrder = getOpenLimitOrderr();
            if (openLimitOrder.getResult().getList().isEmpty()) {
                return;
            }
            String orderId = openLimitOrder.getResult().getList().get(0).getOrderId();
            var client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN).newTradeRestClient();


            var result = client.cancelOrder(TradeOrderRequest.builder().category(CategoryType.LINEAR).symbol("WLDUSDT").orderId(orderId).build());
            System.out.println(result);
        } catch (BybitApiException e) {
            // Обработка ошибок
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    public static BybitLimitOrderResponse getOpenLimitOrderr(String key, String secret) {
        try {
            var client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN).newTradeRestClient();
            var openLinearOrdersResult = client.getOpenOrders(TradeOrderRequest.builder().category(CategoryType.LINEAR).symbol("WLDUSDT").build());
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(objectMapper.writeValueAsString(openLinearOrdersResult), BybitLimitOrderResponse.class);
        } catch (BybitApiException | JsonProcessingException e) {
            // Обработка ошибок
            System.err.println("Ошибка: " + e.getMessage());
        }
        return null;
    }

    @SneakyThrows
    public static void main(String[] args) {
//        closeOpenLimitOrder("x29QaRh6pSDzmTLUAO", "ZGDBtgo5GX1KBoLl1RTjsJk0CWHeIpwgdSxy", true);
        BybitOrderService service = new BybitOrderService(new ObjectMapper());
        while (true) {
//            "10", Pair.of("6KHHWQ26pEBvLGTvNq", "ADD12KPrgwmMBewxeWaWj1dGbLvyooJtLZYB")

            service.openOrder("6KHHWQ26pEBvLGTvNq",
                    "ADD12KPrgwmMBewxeWaWj1dGbLvyooJtLZYB",
                    Symbol.WLD,
                    "2.51",
                    "2",
                    "3",
                    Side.Sell,
                    OrderType.MARKET,
                    UUID.randomUUID(),
                    System.out::println);
            System.out.println(LocalDateTime.now());
            Thread.sleep(300000);
        }
    }
//            "10", Pair.of("XoX4nqAL5ZZxqr3r0j", "TavNLVR6Q6nkbOvGye3JeeEvLNksptTwrIxF")

}

package com.strategy.bot.service;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.TradeOrderType;
import com.bybit.api.client.domain.trade.PositionIdx;
import com.bybit.api.client.domain.trade.Side;
import com.bybit.api.client.domain.trade.TimeInForce;
import com.bybit.api.client.domain.trade.request.TradeOrderRequest;
import com.bybit.api.client.exception.BybitApiException;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.dao.bot.enums.Symbol;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.UUID;

@Service
public class OrderLimitHedgeModeService {

    public UUID openOrder(String key, String secret, Symbol symbol, String tvh, String sl, String tp, String qty,
                          Side side, PositionIdx hedgeMode) {
        try {
            var client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN, true).newTradeRestClient();

            TradeOrderRequest orderRequest = TradeOrderRequest.builder()
                    .category(CategoryType.LINEAR)  // Для USDT perpetual
                    .symbol(symbol.name() + "USDT")          // Торговая пара
                    .side(side)             // Покупка
                    .orderType(TradeOrderType.MARKET)  // Лимитный ордер
                    .qty(qty)                // Количество
                    .price(tvh)
                    .takeProfit(tp)// Цена входа
                    .stopLoss(sl)
                    .tpslMode("Partial")
                    .timeInForce(TimeInForce.GTC) // Good Till Cancel
                    .positionIdx(hedgeMode) // Режим позиции (0 для one-way)
                    .orderLinkId(UUID.randomUUID().toString()) // Кастомный ID ордера
                    .build();


            Object response = client.createOrder(orderRequest);
            System.out.println(response);

            Object orderId = ((LinkedHashMap<?, ?>) ((LinkedHashMap<?, ?>) response).get("result")).get("orderId");
            return UUID.fromString((String) orderId);
        } catch (BybitApiException e) {
            // Обработка ошибок
            System.err.println("Ошибка: " + e.getMessage());
            throw e;
        }

    }

    @SneakyThrows
    public void closeOpenLimitOrder(String key, String secret, Symbol symbol, UUID orderId) {
        try {
            var client = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN).newTradeRestClient();

            var result = client.cancelOrder(TradeOrderRequest.builder()
                    .category(CategoryType.LINEAR)
                    .symbol(symbol.name() + "USDT")
                    .orderId(orderId.toString()).build());
            System.out.println("Отмена лимитной заявки\n" + result);
        } catch (BybitApiException e) {
            // Обработка ошибок
            System.err.println("Ошибка: " + e.getMessage());
            throw new RuntimeException("Не удалось закрыть лимитную заявку");
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
//        closeOpenLimitOrder("x29QaRh6pSDzmTLUAO", "ZGDBtgo5GX1KBoLl1RTjsJk0CWHeIpwgdSxy", true);
        OrderLimitHedgeModeService service = new OrderLimitHedgeModeService();
                                                // main account
//        UUID uuidBuy = service.openOrder("6CKgANrPFtih7TAAI4", "lu5WwteC0SOcT5IDgxYC9gMEFCuFONUIbaOR",
//                Symbol.SOL,
//                "140",
//                "146",
//                "190",
//                "0.1",
//                Side.BUY,
//                PositionIdx.HEDGE_MODE_BUY);


//        System.out.println("buy orderId: " + uuidBuy);
//
//
//        UUID uuidSell = service.openOrder(
//                "Bm93uykPRKyNZqaGeI",
//                "NLrdAqquHmoCjxXU3ynmx6f4XypEq5gOufMe",
//                Symbol.SOL,
//                "150",
//                "155",
//                "144",
//                "0.1",
//                Side.SELL,
//                PositionIdx.HEDGE_MODE_SELL);
//
//        System.out.println("sell orderId: " + uuidSell);

//        service.closeOpenLimitOrder("Bm93uykPRKyNZqaGeI", "NLrdAqquHmoCjxXU3ynmx6f4XypEq5gOufMe",
//                Symbol.SOL, UUID.fromString("bb67fafc-f1b1-414f-b3e7-d20ce8c0be68"));

        CommonUtils.isOpenPosition("Bm93uykPRKyNZqaGeI", "NLrdAqquHmoCjxXU3ynmx6f4XypEq5gOufMe", "SOLUSDT");
//        CommonUtils.isOpenPosition("6CKgANrPFtih7TAAI4", "lu5WwteC0SOcT5IDgxYC9gMEFCuFONUIbaOR", UUID.fromString("7e38821d-c566-4cac-bf96-0b042dba5fc7"), "SOL");
    }

}

package com.strategy.bot.service;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.position.request.PositionDataRequest;
import com.bybit.api.client.domain.trade.request.TradeOrderRequest;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.dao.bot.entity.Deal;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strategy.bot.dto.BybitLimitOrderResponse;
import com.strategy.bot.dto.TradeHistoryResponse;
import lombok.SneakyThrows;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.strategy.bot.service.RangeComparator.areNumbersInRange;
import static com.strategy.bot.startegy.test.BinanceTicks.getDateTime;

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

    @SneakyThrows
    public static Optional<Double> getLastCloseOrder(String key, String secret, Deal deal, String symbol) {
        var clientPos = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN).newPositionRestClient();
        Object res = clientPos.getClosePnlList(PositionDataRequest.builder().category(CategoryType.LINEAR).symbol(symbol).build());
        ObjectMapper objectMapper = new ObjectMapper();
        TradeHistoryResponse bybitLimitOrderResponse = objectMapper.readValue(objectMapper.writeValueAsString(res), TradeHistoryResponse.class);

        // тут мы получили лист закрытых позиций
        List<TradeHistoryResponse.TradeRecord> list = bybitLimitOrderResponse.getResult().getList();

        TradeHistoryResponse.TradeRecord lastCloseOrder = list.get(0);
        double closePrice = lastCloseOrder.getAvgExitPrice();
        double closedSize = lastCloseOrder.getClosedSize();
        double resultPnl = lastCloseOrder.getClosedPnl() - lastCloseOrder.getOpenFee() - lastCloseOrder.getCloseFee();

        double sl = deal.getSl();
        double tp = deal.getTp();
        double vol = deal.getVol();

        boolean inRangeSl = areNumbersInRange(closePrice, sl);
        boolean inRangeTp = areNumbersInRange(closePrice, tp);

        if (closedSize == vol && (inRangeTp || inRangeSl)) {
            Optional.of(resultPnl);
        }
        return Optional.empty();
    }

    @SneakyThrows
    public static boolean isOpenPosition(String key, String secret, String symbol) {
        List<TradeHistoryResponse.TradeRecord> closeOrderBySymbol = getCloseOrderBySymbol(key, secret, symbol);
        closeOrderBySymbol.stream()
//                .map(TradeHistoryResponse.TradeRecord::getClosedPnl)
                .forEach(s -> {
                    // это уже с учетом комиссии
                    System.out.println("res: : " + s.getClosedPnl());
                    System.out.println("vol: : " + s.getClosedSize());
                    System.out.println("createTime: " + getDateTime(s.getCreatedTime()));
                    System.out.println("updateTime: " + getDateTime(s.getUpdatedTime()) + "\n");
                });
        TradeHistoryResponse.TradeRecord tradeRecord = closeOrderBySymbol.get(0);

        System.out.println(closeOrderBySymbol.get(1));
        return true;
    }

    @SneakyThrows
    public static boolean isOpenPosition(String key, String secret, Deal deal) {
        List<TradeHistoryResponse.TradeRecord> closeOrderBySymbol = getCloseOrderBySymbol(key, secret, deal.getSymbol().name() + "USDT");
        TradeHistoryResponse.TradeRecord tradeRecord = closeOrderBySymbol.get(0);
        double avgExitPrice = tradeRecord.getAvgExitPrice();
        double sl = deal.getSl();
        double tp = deal.getTp();
        if (deal.getVol() == tradeRecord.getClosedSize()) {
            if (areNumbersInRange(avgExitPrice, sl) && deal.getVol() == tradeRecord.getClosedSize()) {
                deal.setCloseDate(LocalDateTime.now());
                deal.setCurrentResult(tradeRecord.getClosedPnl());
                System.out.println("закрытие по sl");
                System.out.println(tradeRecord);
                System.out.println(deal);
                return false;
            } else if (areNumbersInRange(avgExitPrice, tp)) {
                deal.setCloseDate(LocalDateTime.now());
                deal.setCurrentResult(tradeRecord.getClosedPnl());
                System.out.println("закрытие по tp");
                System.out.println(tradeRecord);
                System.out.println(deal);
                return false;
            }
        }
        return true;
    }

    @SneakyThrows
    public static List<TradeHistoryResponse.TradeRecord> getCloseOrderBySymbol(String key, String secret, String symbol) {
        var clientPos = BybitApiClientFactory.newInstance(key, secret, BybitApiConfig.MAINNET_DOMAIN).newPositionRestClient();
        Object res = clientPos.getClosePnlList(PositionDataRequest.builder().category(CategoryType.LINEAR).symbol(symbol).build());
        ObjectMapper objectMapper = new ObjectMapper();
        TradeHistoryResponse bybitLimitOrderResponse = objectMapper.readValue(objectMapper.writeValueAsString(res), TradeHistoryResponse.class);
        return bybitLimitOrderResponse.getResult().getList();
    }

//    TradeHistoryResponse.TradeRecord(symbol=SOLUSDT, orderType=Market, leverage=75, updatedTime=1751727422522,
//    side=Buy, orderId=818781a6-5b3c-4dd9-bc0e-f4bda4aad371,
//    closedPnl=-0.258252,
//    openFee=0.059056,
//    closeFee=0.059196, avgEntryPrice=147.64,
//    qty=0.4, cumEntryValue=59.056, createdTime=1751727422519, orderPrice=155.41,
//    closedSize=0.4,
//    avgExitPrice=147.99, execType=Trade, fillCount=1, cumExitValue=59.196)

//    TradeHistoryResponse.TradeRecord(symbol=SOLUSDT, orderType=Market, leverage=100, updatedTime=1752123000660, side=Sell, orderId=64ae5fda-49b1-4fbd-ba5b-224434fb9443, closedPnl=-0.011496, openFee=0.015738, closeFee=0.015758, avgEntryPrice=157.38, qty=0.1, cumEntryValue=15.738, createdTime=1752123000657, orderPrice=149.69, closedSize=0.1,
//    avgExitPrice=157.58, execType=Trade, fillCount=1, cumExitValue=15.758)


//    TradeHistoryResponse.TradeRecord(symbol=SOLUSDT, orderType=Market, leverage=100,
//    updatedTime=1752114369850, side=Sell, orderId=f0c364da-2c1d-4daa-b2ba-0377c0eb5f49,
//    closedPnl=0.64860039, openFee=0.00548082, closeFee=0.015897,
//    avgEntryPrice=152.245, qty=0.1, cumEntryValue=15.2245, createdTime=1752024478295, orderPrice=150.95,
//    closedSize=0.1,
//    avgExitPrice=158.97, execType=Trade, fillCount=1, cumExitValue=15.897)

//    TradeHistoryResponse.TradeRecord(symbol=SOLUSDT, orderType=Market, leverage=100,
//    updatedTime=1752185990241, side=Buy, orderId=6c588245-aab0-4cff-b201-090fb9a54746,
//    closedPnl=-0.2333722, openFee=0.0058662, closeFee=0.016506,
//    avgEntryPrice=162.95, qty=0.1, cumEntryValue=16.295, createdTime=1752182368308, orderPrice=173.23,
//    closedSize=0.1,
//    avgExitPrice=165.06, execType=Trade, fillCount=1, cumExitValue=16.506)


//    TradeHistoryResponse.TradeRecord(symbol=SOLUSDT, orderType=Market, leverage=100,
//    updatedTime=1752182252028, side=Sell, orderId=3acf427e-2aba-47d5-9249-417a39a7b94a,
//    closedPnl=0.91818486, openFee=0.00548082, closeFee=0.01617, avgEntryPrice=152.245, qty=0.1,
//    cumEntryValue=15.2245, createdTime=1752039966221, orderPrice=153.56,
//    closedSize=0.1, avgExitPrice=161.7, execType=Trade, fillCount=1, cumExitValue=16.17)
}

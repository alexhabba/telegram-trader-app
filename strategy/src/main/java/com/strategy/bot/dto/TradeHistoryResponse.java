package com.strategy.bot.dto;

import lombok.Data;
import java.util.List;

//    TradeHistoryResponse.TradeRecord(symbol=SOLUSDT, orderType=Market, leverage=100,
//    updatedTime=1752182252028, side=Sell, orderId=3acf427e-2aba-47d5-9249-417a39a7b94a,
//    closedPnl=0.91818486, openFee=0.00548082, closeFee=0.01617, avgEntryPrice=152.245, qty=0.1,
//    cumEntryValue=15.2245, createdTime=1752039966221, orderPrice=153.56,
//    closedSize=0.1, avgExitPrice=161.7, execType=Trade, fillCount=1, cumExitValue=16.17)
@Data
public class TradeHistoryResponse {
    private int retCode;
    private String retMsg;
    private Result result;
    private RetExtInfo retExtInfo;
    private long time;

    @Data
    public static class Result {
        private String nextPageCursor;
        private String category;
        private List<TradeRecord> list;
    }

    @Data
    public static class TradeRecord {
        private String symbol;
        private String orderType;
        private int leverage;
        private long updatedTime;
        private String side;
        private String orderId;
        private double closedPnl;
        private double openFee;
        private double closeFee;
        private double avgEntryPrice;
        private double qty;
        private double cumEntryValue;
        private long createdTime;
        private double orderPrice;
        private double closedSize;
        // цена выхода из позиции
        private double avgExitPrice;
        private String execType;
        private int fillCount;
        private double cumExitValue;
    }

    @Data
    public static class RetExtInfo {
        // Пустой класс, так как в JSON retExtInfo пустой объект
    }
}
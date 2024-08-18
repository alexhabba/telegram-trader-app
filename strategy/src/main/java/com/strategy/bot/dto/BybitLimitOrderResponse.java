package com.strategy.bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BybitLimitOrderResponse {

    private int retCode;
    private String retMsg;
    private Result result;
    private RetExtInfo retExtInfo;
    private long time;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {

        private String nextPageCursor;
        private String category;
        private List<Order> list;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Order {
            private String symbol;
            private String orderType;
            private String orderLinkId;
            private BigDecimal slLimitPrice;
            private String orderId;
            private String cancelType;
            private BigDecimal avgPrice;
            private String stopOrderType;
            private BigDecimal lastPriceOnCreated;
            private String orderStatus;
            private String createType;
            private BigDecimal takeProfit;
            private BigDecimal cumExecValue;
            private String tpslMode;
            private String smpType;
            private int triggerDirection;
            private String blockTradeId;
            private Boolean isLeverage;
            private String rejectReason;
            private BigDecimal price;
            private String orderIv;
            private long createdTime;
            private String tpTriggerBy;
            private int positionIdx;
            private String timeInForce;
            private BigDecimal leavesValue;
            private long updatedTime;
            private String side;
            private int smpGroup;
            private BigDecimal triggerPrice;
            private BigDecimal tpLimitPrice;
            private BigDecimal cumExecFee;
            private BigDecimal leavesQty;
            private String slTriggerBy;
            private boolean closeOnTrigger;
            private String placeType;
            private BigDecimal cumExecQty;
            private boolean reduceOnly;
            private BigDecimal qty;
            private BigDecimal stopLoss;
            private String marketUnit;
            private String smpOrderId;
            private String triggerBy;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RetExtInfo {
        // Добавьте поля, если они необходимы
    }
}

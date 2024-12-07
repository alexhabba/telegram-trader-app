package com.strategy.bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponsePosition {
    private int retCode;
    private String retMsg;
    private Result result;
    private Map<String, Object> retExtInfo;
    private long time;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private String nextPageCursor;
        private String category;
        @JsonProperty("list")
        private List<Position> positions;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Position {
        private String symbol;
        private String leverage;
        private int autoAddMargin;
        private BigDecimal avgPrice;
        private BigDecimal liqPrice;
        private String riskLimitValue;
        private BigDecimal takeProfit;
        private String positionValue;
        private boolean isReduceOnly;
        private String tpslMode;
        private int riskId;
        private String trailingStop;
        // Нереализованная прибыль
        private String unrealisedPnl;
        private String markPrice;
        private int adlRankIndicator;
        private String cumRealisedPnl;
        private String positionMM;
        private String createdTime;
        // Если 2, то Sell позиция
        // Если 1, то Buy позиция
        private int positionIdx;
        private String positionIM;
        private long seq;
        private String updatedTime;
        private String side;
        private String bustPrice;
        private String positionBalance;
        private String leverageSysUpdatedTime;
        private String curRealisedPnl;
        private BigDecimal size;
        private String positionStatus;
        private String mmrSysUpdatedTime;
        private BigDecimal stopLoss;
        private int tradeMode;
        private String sessionAvgPrice;

        @Override
        public String toString() {
            return "Position{" +
                    "symbol='" + symbol + '\'' +
                    ", leverage='" + leverage + '\'' +
                    ", autoAddMargin=" + autoAddMargin +
                    ", avgPrice=" + avgPrice +
                    ", liqPrice=" + liqPrice +
                    ", riskLimitValue='" + riskLimitValue + '\'' +
                    ", takeProfit=" + takeProfit +
                    ", positionValue='" + positionValue + '\'' +
                    ", isReduceOnly=" + isReduceOnly +
                    ", tpslMode='" + tpslMode + '\'' +
                    ", riskId=" + riskId +
                    ", trailingStop='" + trailingStop + '\'' +
                    ", unrealisedPnl='" + unrealisedPnl + '\'' +
                    ", markPrice='" + markPrice + '\'' +
                    ", adlRankIndicator=" + adlRankIndicator +
                    ", cumRealisedPnl='" + cumRealisedPnl + '\'' +
                    ", positionMM='" + positionMM + '\'' +
                    ", createdTime='" + createdTime + '\'' +
                    ", positionIdx=" + positionIdx +
                    ", positionIM='" + positionIM + '\'' +
                    ", seq=" + seq +
                    ", updatedTime='" + updatedTime + '\'' +
                    ", side='" + side + '\'' +
                    ", bustPrice='" + bustPrice + '\'' +
                    ", positionBalance='" + positionBalance + '\'' +
                    ", leverageSysUpdatedTime='" + leverageSysUpdatedTime + '\'' +
                    ", curRealisedPnl='" + curRealisedPnl + '\'' +
                    ", size=" + size +
                    ", positionStatus='" + positionStatus + '\'' +
                    ", mmrSysUpdatedTime='" + mmrSysUpdatedTime + '\'' +
                    ", stopLoss=" + stopLoss +
                    ", tradeMode=" + tradeMode +
                    ", sessionAvgPrice='" + sessionAvgPrice + '\'' +
                    '}';
        }

//        @Override
//        public String toString() {
//            return "Position{" +
//                    "symbol='" + symbol + '\'' +
//                    ", avgPrice=" + avgPrice +
//                    ", leverage=" + leverage +
//                    ", liqPrice=" + liqPrice +
//                    ", takeProfit=" + takeProfit +
//                    ", markPrice='" + markPrice + '\'' +
//                    ", side='" + side + '\'' +
//                    ", size=" + size +
//                    ", stopLoss=" + stopLoss +
//                    '}';
//        }
    }
}

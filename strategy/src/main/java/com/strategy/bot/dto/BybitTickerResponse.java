package com.strategy.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BybitTickerResponse {
    @JsonProperty("retCode")
    private int retCode;

    @JsonProperty("retMsg")
    private String retMsg;

    @JsonProperty("result")
    private Result result;

    @JsonProperty("retExtInfo")
    private Object retExtInfo;

    @JsonProperty("time")
    private long time;

    public static class Result {
        @JsonProperty("category")
        private String category;

        @JsonProperty("list")
        private List<TickerData> list;

        // Геттеры и сеттеры
        public String getCategory() { return category; }
        public List<TickerData> getList() { return list; }
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    public static class TickerData {
        @JsonProperty("symbol")
        private String symbol;

        @JsonProperty("lastPrice")
        private String lastPrice;

        @JsonProperty("indexPrice")
        private String indexPrice;

        @JsonProperty("markPrice")
        private String markPrice;

        @JsonProperty("prevPrice24h")
        private String prevPrice24h;

        @JsonProperty("price24hPcnt")
        private String price24hPcnt;

        @JsonProperty("highPrice24h")
        private String highPrice24h;

        @JsonProperty("lowPrice24h")
        private String lowPrice24h;

        @JsonProperty("prevPrice1h")
        private String prevPrice1h;

        @JsonProperty("openInterest")
        private String openInterest;

        @JsonProperty("openInterestValue")
        private String openInterestValue;

        @JsonProperty("turnover24h")
        private String turnover24h;

        @JsonProperty("volume24h")
        private String volume24h;

        @JsonProperty("fundingRate")
        private String fundingRate;

        @JsonProperty("nextFundingTime")
        private long nextFundingTime;

        @JsonProperty("predictedDeliveryPrice")
        private String predictedDeliveryPrice;

        @JsonProperty("basisRate")
        private String basisRate;

        @JsonProperty("deliveryFeeRate")
        private String deliveryFeeRate;

        @JsonProperty("deliveryTime")
        private long deliveryTime;

        @JsonProperty("ask1Size")
        private String ask1Size;

        @JsonProperty("bid1Price")
        private String bid1Price;

        @JsonProperty("ask1Price")
        private String ask1Price;

        @JsonProperty("bid1Size")
        private String bid1Size;

        // Геттеры с конвертацией времени
        public LocalDateTime getNextFundingDateTime() {
            return Instant.ofEpochMilli(nextFundingTime)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

        public LocalDateTime getDeliveryDateTime() {
            return Instant.ofEpochMilli(deliveryTime)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

    }

    // Геттеры
    public LocalDateTime getTime() {
        return Instant.ofEpochMilli(time)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public int getRetCode() { return retCode; }
    public String getRetMsg() { return retMsg; }
    public Result getResult() { return result; }
}
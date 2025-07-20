package com.strategy.bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BybitInstrumentsResponse {
    @JsonProperty("retCode")
    private Integer retCode;

    @JsonProperty("retMsg")
    private String retMsg;

    @JsonProperty("result")
    private Result result;

    @JsonProperty("retExtInfo")
    private Object retExtInfo;

    @JsonProperty("time")
    private Long time;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        @JsonProperty("category")
        private String category;

        @JsonProperty("list")
        private List<InstrumentInfo> list;

        @JsonProperty("nextPageCursor")
        private String nextPageCursor;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InstrumentInfo {
        @JsonProperty("symbol")
        private String symbol;

        @JsonProperty("contractType")
        private String contractType;

        @JsonProperty("status")
        private String status;

        @JsonProperty("baseCoin")
        private String baseCoin;

        @JsonProperty("quoteCoin")
        private String quoteCoin;

        @JsonProperty("launchTime")
        private String launchTime;

        @JsonProperty("deliveryTime")
        private String deliveryTime;

        @JsonProperty("priceScale")
        private String priceScale;

        @JsonProperty("leverageFilter")
        private LeverageFilter leverageFilter;

        @JsonProperty("priceFilter")
        private PriceFilter priceFilter;

        @JsonProperty("lotSizeFilter")
        private LotSizeFilter lotSizeFilter;

        @JsonProperty("upperFundingRate")
        private String upperFundingRate;

        @JsonProperty("lowerFundingRate")
        private String lowerFundingRate;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)

        public static class LeverageFilter {
            @JsonProperty("minLeverage")
            private String minLeverage;

            @JsonProperty("maxLeverage")
            private String maxLeverage;

            @JsonProperty("leverageStep")
            private String leverageStep;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)

        public static class PriceFilter {
            @JsonProperty("minPrice")
            private String minPrice;

            @JsonProperty("maxPrice")
            private String maxPrice;

            @JsonProperty("tickSize")
            private String tickSize;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        
        public static class LotSizeFilter {
            @JsonProperty("maxOrderQty")
            private String maxOrderQty;

            @JsonProperty("minOrderQty")
            private String minOrderQty;

            @JsonProperty("qtyStep")
            private String qtyStep;
        }
    }
}
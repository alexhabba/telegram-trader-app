package com.trade.bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FutureBybitTradeDto {

    private String topic;
    private String type;
    private long ts;
    private List<FutureTradeDataDto> data;

    @Data
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FutureTradeDataDto {

        @JsonProperty("T")
        private long time;
        @JsonProperty("s")
        private String symbol;
        @JsonProperty("S")
        private String operation;
        private String v;
        private String p;
        @JsonProperty("L")
        private String tradeLastTick;
        private String i;
        private boolean BT;
    }
}



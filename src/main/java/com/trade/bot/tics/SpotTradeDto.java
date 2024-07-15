package com.trade.bot.tics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotTradeDto {

    private String topic;
    private Long ts;
    private String type;
    private SpotTradeData data;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SpotTradeData {
        private String v;
        private Long t;
        private String p;
        private String q;
        private boolean m;
        private String type;
    }
}

package com.trade.bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * An aggregated trade event for a symbol.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotTradeDto {

  @JsonProperty("e")
  private String eventType;

  @JsonProperty("E")
  private long eventTime;

  @JsonProperty("s")
  private String symbol;

  @JsonProperty("a")
  private long aggregatedTradeId;

  @JsonProperty("p")
  private String price;

  @JsonProperty("q")
  private String quantity;

  @JsonProperty("f")
  private long firstBreakdownTradeId;

  @JsonProperty("l")
  private long lastBreakdownTradeId;

  @JsonProperty("T")
  private long tradeTime;

  @JsonProperty("m")
  private boolean isBuyerMaker;

}

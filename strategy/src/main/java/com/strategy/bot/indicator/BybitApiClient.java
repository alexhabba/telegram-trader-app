package com.strategy.bot.indicator;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.market.request.MarketDataRequest;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strategy.bot.dto.BybitInstrumentsResponse;
import com.strategy.bot.dto.BybitTickerResponse;
import com.strategy.bot.service.FundingRateLogger;
import com.strategy.bot.service.SolLogger;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BybitApiClient {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    //    {"retCode":0,"retMsg":"OK","result":{"category":"linear","list":[{"symbol":"XTZUSDT","fundingRate":"-0.00249093","fundingRateTimestamp":"1752940800000"},{"symbol":"XTZUSDT","fundingRate":"-0.00073488","fundingRateTimestamp":"1752912000000"},{"symbol":"XTZUSDT","fundingRate":"0.0001","fundingRateTimestamp":"1752883200000"},{"symbol":"XTZUSDT","fundingRate":"0.0001","fundingRateTimestamp":"1752854400000"},{"symbol":"XTZUSDT","fundingRate":"0.0001","fundingRateTimestamp":"1752825600000"},{"symbol":"XTZUSDT","fundingRate":"0.0001","fundingRateTimestamp":"1752796800000"},{"symbol":"XTZUSDT","fundingRate":"-0.00012402","fundingRateTimestamp":"1752768000000"},{"symbol":"XTZUSDT","fundingRate":"-0.00008311","fundingRateTimestamp":"1752739200000"},{"symbol":"XTZUSDT","fundingRate":"0.0001","fundingRateTimestamp":"1752710400000"},{"symbol":"XTZUSDT","fundingRate":"0.0001","fundingRateTimestamp":"1752681600000"}]},"retExtInfo":{},"time":1752954048348}
    // Парсинг JSON
    public BybitTickerResponse parseFundingRate(Object object) throws IOException {
        return objectMapper.readValue(objectMapper.writeValueAsString(object), BybitTickerResponse.class);
    }


    public static void main(String[] args) throws IOException, InterruptedException {

        var client = BybitApiClientFactory.newInstance("Bm93uykPRKyNZqaGeI", "NLrdAqquHmoCjxXU3ynmx6f4XypEq5gOufMe", BybitApiConfig.MAINNET_DOMAIN).newMarketDataRestClient();
        Object instruments = client.getInstrumentsInfo(MarketDataRequest.builder().category(CategoryType.LINEAR).limit(1000).build());
        BybitInstrumentsResponse instrumentInfo = objectMapper.readValue(objectMapper.writeValueAsString(instruments), BybitInstrumentsResponse.class);
        List<String> symbols = instrumentInfo.getResult().getList().stream()
                .map(BybitInstrumentsResponse.InstrumentInfo::getSymbol)
                .filter(symbol -> symbol.endsWith("USDT"))
                .collect(Collectors.toList());

//        symbols.forEach(System.out::println);

        new Thread(() -> {
            while (true) {

                try {
                    String s = "SOLUSDT";
                    Object obj = client.getMarketTickers(MarketDataRequest.builder().category(CategoryType.LINEAR).symbol(s).build());
                    BybitTickerResponse bybitTickerResponse = objectMapper.readValue(objectMapper.writeValueAsString(obj), BybitTickerResponse.class);
                    BybitTickerResponse.TickerData tickerData = bybitTickerResponse.getResult().getList().get(0);
                    double fundingRate = Double.parseDouble(tickerData.getFundingRate());
                    String markPrice = tickerData.getMarkPrice();
                    double openInterestValue = Double.parseDouble(tickerData.getOpenInterestValue());

                    SolLogger.logToFile(s, openInterestValue, fundingRate, markPrice);
                } catch (Exception e) {
                    log.error("Error message: {}", e.getMessage(), e);
                }
                try {
                    Thread.sleep(1000 * 60 * 5);
                } catch (InterruptedException e) {
                    log.error("Error message: {}", e.getMessage(), e);
                }
            }
        }).start();

        while (true) {
            symbols.forEach(s -> {
                try {
                    Object obj = client.getMarketTickers(MarketDataRequest.builder().category(CategoryType.LINEAR).symbol(s).build());
                    BybitTickerResponse bybitTickerResponse = objectMapper.readValue(objectMapper.writeValueAsString(obj), BybitTickerResponse.class);
                    BybitTickerResponse.TickerData tickerData = bybitTickerResponse.getResult().getList().get(0);
                    double fundingRate = Double.parseDouble(tickerData.getFundingRate());
                    String markPrice = tickerData.getMarkPrice();
                    double openInterestValue = Double.parseDouble(tickerData.getOpenInterestValue());

                    if (Math.abs(fundingRate * 100) > 0.5) {
                        String link = "https://www.bybit.com/trade/usdt/" + s;
                        FundingRateLogger.logToFile(s, openInterestValue, fundingRate, markPrice);
                        System.out.println(s + "    " + fundingRate + "     " + LocalDateTime.now() + "     " + link);
                    }
                } catch (Exception e) {
                    log.error("Error message: {}", e.getMessage(), e);
                }
            });
            Thread.sleep(1000 * 60 * 5);
        }

//        System.out.println("конец");

    }
}

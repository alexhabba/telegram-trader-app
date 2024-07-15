package com.trade.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Component
//@RequiredArgsConstructor
public class BybitTradeFetcher {

    private final WebClient client = WebClient.create("https://api.bybit.com/spot/v3");

    public String fetchTrades() {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/public/quote/trades")
                        .queryParam("symbol", "BTCUSDT")
                        .queryParam("limit", 100)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}

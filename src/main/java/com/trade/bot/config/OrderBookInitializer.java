package com.trade.bot.config;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.websocket_message.public_channel.PublicOrderBookData;
import com.bybit.api.client.domain.websocket_message.public_channel.WebsocketOrderbookMessage;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.bot.bot.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
//@Component
@RequiredArgsConstructor
public class OrderBookInitializer {

    @Value("${list.symbol}")
    private final List<String> symbols;
    private final TelegramBot telegramBot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        symbols.forEach(this::connectWebSocket);
    }

    private void connectWebSocket(String str) {
        var client = BybitApiClientFactory.newInstance(BybitApiConfig.STREAM_MAINNET_DOMAIN, true, "okhttp3").newWebsocketClient(20);
        client.setMessageHandler(telegramBot::handler);
        client.getPublicChannelStream(List.of("orderbook.200." + str + "USDT"), BybitApiConfig.V5_PUBLIC_SPOT);
    }
}

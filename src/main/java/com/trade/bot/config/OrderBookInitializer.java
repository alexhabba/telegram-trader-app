package com.trade.bot.config;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.service.BybitApiClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.List;

@Slf4j
//@Component
@RequiredArgsConstructor
public class OrderBookInitializer {

    @Value("${list.symbol}")
    private final List<String> symbols;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        symbols.forEach(this::connectWebSocket);
    }

    private void connectWebSocket(String str) {
        var client = BybitApiClientFactory.newInstance(BybitApiConfig.STREAM_MAINNET_DOMAIN, true, "okhttp3").newWebsocketClient(20);
        client.getPublicChannelStream(List.of("orderbook.200." + str + "USDT"), BybitApiConfig.V5_PUBLIC_SPOT);
    }
}

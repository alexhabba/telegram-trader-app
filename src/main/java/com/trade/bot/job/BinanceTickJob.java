package com.trade.bot.job;

import com.bybit.api.client.websocket.callback.WebSocketClosedCallback;
import com.bybit.api.client.websocket.callback.WebSocketClosingCallback;
import com.bybit.api.client.websocket.callback.WebSocketFailureCallback;
import com.dao.bot.entity.Tick;
import com.dao.bot.enums.Side;
import com.dao.bot.enums.Symbol;
import com.dao.bot.repository.TickRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.bot.dto.SpotTradeDto;
import com.trade.bot.websocket.WebSocketConnection;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.trade.bot.utils.DateTimeUtils.getDateTime;
import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class BinanceTickJob {

    public static final String WS_API_BASE_URL = "wss://stream.binance.com:9443/ws";
    public static final String POSTFIX = "/bnbusdt@aggTrade/notusdt@aggTrade/ethusdt@aggTrade/wldusdt@aggTrade/btcusdt@aggTrade/solusdt@aggTrade/tonusdt@aggTrade";

    private final ObjectMapper objectMapper;
    private final TickRepository tickRepository;
    private List<Tick> ticks = new ArrayList<>();

    @EventListener({ContextRefreshedEvent.class})
    @SneakyThrows
    public void init() {
        connect();
    }
//    https://data.binance.vision/?prefix=data/spot/daily/aggTrades/SOLUSDT/#:~:text=06T03%3A29%3A01.000Z-,SOLUSDT%2DaggTrades%2D2025%2D01%2D05.zip,-4.6%20MB
    public void connect() {
        //      /bnbusdt@aggTrade/notusdt@aggTrade/ethusdt@aggTrade/wldusdt@aggTrade/btcusdt@aggTrade/solusdt@aggTrade/tonusdt@aggTrade
        String path = "/" + Arrays.stream(Symbol.values())
                .map(Symbol::name)
                .map(String::toLowerCase)
                .collect(Collectors.joining("usdt@aggTrade/")) + "usdt@aggTrade";

        OkHttpClient client = new OkHttpClient();

        new WebSocketConnection(
                System.out::println,
                this::handlerMessage,
                getWebSocketClosingCallback(),
                getWebSocketClosedCallback(),
                getWebSocketFailureCallback(),
                WS_API_BASE_URL + path,
                client
        );
    }
//    bad
//    url=https://stream.binance.com:9443/ws/not@aggTrade/wld@aggTrade/btc@aggTrade/sol@aggTrade/ton@aggTrade/eth@aggTrade/bnb@aggTrade}
//    url=https://stream.binance.com:9443/ws/bnbusdt@aggTrade/notusdt@aggTrade/ethusdt@aggTrade/wldusdt@aggTrade/btcusdt@aggTrade/solusdt@aggTrade/tonusdt@aggTrade}
    public void handlerMessage(String message) {
        SpotTradeDto trade = null;
        try {
            trade = objectMapper.readValue(message, SpotTradeDto.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return;
        }

        if (isNull(trade)) {
            return;
        }

        String q = Double.valueOf(trade.getQuantity()).toString();
        String p = Double.valueOf(trade.getPrice()).toString();
        String symbol = trade.getSymbol();

        Tick tick = Tick.builder()
                .quantity(q)
                .price(p)
                .side(Side.valueOf(trade.isBuyerMaker() ? "Sell" : "Buy"))
                .createDate(getDateTime(trade.getTradeTime()))
                .symbol(Symbol.valueOf(symbol.replace("USDT", "")))
                .exchange("binance")
                .instrument("spot")
                .build();

        ticks.add(tick);
    }

    public WebSocketClosingCallback getWebSocketClosingCallback() {
        return (x, y) -> {
            log.debug("error {} {}", x, y);
            connect();
        };
    }

    public WebSocketClosedCallback getWebSocketClosedCallback() {
        return (x, y) -> {
            log.debug("error {} {}", x, y);
            connect();
        };
    }

    public WebSocketFailureCallback getWebSocketFailureCallback() {
        return (x, y) -> {
            log.debug("error {} {}", x, y);
            connect();
        };
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void saveTick() {
        List<Tick> ticks1 = new ArrayList<>(ticks);
        ticks = new ArrayList<>();
        tickRepository.saveAll(ticks1);
    }
}

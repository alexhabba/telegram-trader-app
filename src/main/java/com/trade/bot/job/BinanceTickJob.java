package com.trade.bot.job;

import com.bybit.api.client.websocket.callback.WebSocketClosedCallback;
import com.bybit.api.client.websocket.callback.WebSocketClosingCallback;
import com.bybit.api.client.websocket.callback.WebSocketFailureCallback;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.bot.dto.SpotTradeDto;
import com.trade.bot.entity.Tick;
import com.trade.bot.enums.Side;
import com.trade.bot.enums.Symbol;
import com.trade.bot.repository.TickRepository;
import com.trade.bot.websocket.WebSocketConnection;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import static com.trade.bot.utils.DateTimeUtils.getDateTime;
import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class BinanceTickJob {

    public static final String WS_API_BASE_URL = "wss://stream.binance.com:9443/ws";
    public static final String POSTFIX = "/wldusdt@aggTrade";

    private final ObjectMapper objectMapper;
    private final TickRepository tickRepository;

    @EventListener({ContextRefreshedEvent.class})
    @SneakyThrows
    public void init() {
        connect();
    }

    public void connect() {
        OkHttpClient client = new OkHttpClient();

        new WebSocketConnection(
                System.out::println,
                this::handlerMessage,
                getWebSocketClosingCallback(),
                getWebSocketClosedCallback(),
                getWebSocketFailureCallback(),
                WS_API_BASE_URL + POSTFIX,
                client
        );
    }

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

        System.out.println(tick);
        tickRepository.save(tick);
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

}

package com.trade.bot.handler;

import com.bybit.api.client.websocket.callback.WebSocketClosedCallback;
import com.bybit.api.client.websocket.callback.WebSocketClosingCallback;
import com.bybit.api.client.websocket.callback.WebSocketFailureCallback;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.bot.dto.FutureBybitTradeDto;
import com.trade.bot.entity.Tick;
import com.trade.bot.enums.Side;
import com.trade.bot.enums.Symbol;
import com.trade.bot.enums.TradeLastTick;
import com.trade.bot.repository.TickRepository;
import com.trade.bot.websocket.WebSocketConnection;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.json.JSONObject;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import static com.trade.bot.utils.DateTimeUtils.getDateTime;
import static java.util.Objects.isNull;

@Slf4j
//@Service
@RequiredArgsConstructor
public class BybitTickHandler {

    private final ObjectMapper objectMapper;
    private final TickRepository tickRepository;

    @EventListener({ContextRefreshedEvent.class})
    @SneakyThrows
    public void init() {
        connect();
    }

    public void connect() {
        OkHttpClient client = new OkHttpClient();

        String serverConnect = "wss://stream.bybit.com/contract/usdt/public/v3";

        WebSocketConnection webSocketConnection = new WebSocketConnection(
                System.out::println,
                this::handlerMessage,
                getWebSocketClosingCallback(),
                getWebSocketClosedCallback(),
                getWebSocketFailureCallback(),
                serverConnect,
                client
        );
        webSocketConnection.send(subscribe());
    }

    public void handlerMessage(String message) {
        // Обработка входящего сообщения
        FutureBybitTradeDto futureBybitTradeDto = null;
        try {
            futureBybitTradeDto = objectMapper.readValue(message, FutureBybitTradeDto.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return;
        }

        if (isNull(futureBybitTradeDto) || isNull(futureBybitTradeDto.getData())) {
            return;
        }

        futureBybitTradeDto.getData().forEach(el -> {
            String q = el.getV();
            String operation = el.getOperation();
            String p = el.getP();
            String symbol = el.getSymbol();

            double count = Double.parseDouble(q);

            Tick tick = Tick.builder()
                    .quantity(q)
                    .price(p)
                    .side(Side.valueOf(operation))
                    .createDate(getDateTime(el.getTime()))
                    .symbol(Symbol.valueOf(symbol.replace("USDT", "")))
                    .lastTick(TradeLastTick.valueOf(el.getTradeLastTick()))
                    .exchange("bybit")
                    .instrument("features")
                    .build();

                tickRepository.save(tick);
        });
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
            log.debug("error {} {}", x.toString(), y.toString());
            connect();
        };
    }

    private String subscribe() {
        JSONObject request = new JSONObject();
        request.put("op", "subscribe");
        request.put("args", new String[]{"publicTrade.WLDUSDT"});
        return request.toString();
    }
}

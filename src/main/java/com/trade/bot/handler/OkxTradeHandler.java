package com.trade.bot.handler;

import com.bybit.api.client.websocket.callback.WebSocketClosedCallback;
import com.bybit.api.client.websocket.callback.WebSocketClosingCallback;
import com.bybit.api.client.websocket.callback.WebSocketFailureCallback;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.trade.bot.dto.SpotOkxTradeDto;
import com.trade.bot.entity.Tick;
import com.trade.bot.enums.OrderSide;
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
public class OkxTradeHandler {

    private final ObjectMapper objectMapper;
    private final TickRepository tickRepository;

    @EventListener({ContextRefreshedEvent.class})
    @SneakyThrows
    public void init() {
        connect();
    }

    public void connect() {
        OkHttpClient client = new OkHttpClient();

        String serverConnect = "wss://ws.okx.com:8443/ws/v5/public";

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
        SpotOkxTradeDto spotOkxTradeDto = null;
        try {
            spotOkxTradeDto = objectMapper.readValue(message, SpotOkxTradeDto.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return;
        }

        if (isNull(spotOkxTradeDto) || isNull(spotOkxTradeDto.getData())) {
            return;
        }

        spotOkxTradeDto.getData().forEach(el -> {
            String q = el.getSz();
            String operation = el.getSide();
            String p = el.getPx();
            String symbol = el.getInstId();

            Tick tick = Tick.builder()
                    .quantity(q)
                    .price(p)
                    .side(OrderSide.valueOf(operation.equals("sell") ? "Sell" : "Buy"))
                    .createDate(getDateTime(el.getTs()))
                    .symbol(Symbol.valueOf(symbol.replace("-USDT", "")))
                    .exchange("okx")
                    .instrument("spot")
                    .build();

            tickRepository.save(tick);
        });
    }

    public WebSocketClosingCallback getWebSocketClosingCallback() {
        return (x, y) -> {
            log.debug("error {} {}", x, y);
            connect();
        };    }

    public WebSocketClosedCallback getWebSocketClosedCallback() {
        return (x, y) -> {
            log.debug("error {} {}", x, y);
            connect();
        };    }

    public WebSocketFailureCallback getWebSocketFailureCallback() {
        return (x, y) -> {
            log.debug("error {} {}", x.toString(), y.toString());
            connect();
        };
    }

    private String subscribe() {
        // Создаем объект Gson
        Gson gson = new GsonBuilder().create();

        // Создаем JSON-объект для параметров
        JsonObject params = new JsonObject();
        params.addProperty("channel", "trades");
        params.addProperty("instType", "FUTURES");
        params.addProperty("instId", "WLD-USDT");

        // Создаем JSON-массив для аргументов
        JsonArray args = new JsonArray();
        args.add(params);

        // Создаем JSON-объект для запроса
        JsonObject request = new JsonObject();
        request.add("args", args);
        request.addProperty("op", "subscribe");

        return gson.toJson(request);
    }
}

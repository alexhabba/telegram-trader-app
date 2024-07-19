package com.trade.bot.tics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.trade.bot.dto.SpotOkxTradeDto;
import com.trade.bot.entity.Tick;
import com.trade.bot.enums.Side;
import com.trade.bot.enums.Symbol;
import com.trade.bot.repository.TickRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import static com.trade.bot.utils.DateTimeUtils.getDateTime;
import static java.util.Objects.isNull;

@Slf4j
public class OkxWebSocketClientSpot extends WebSocketClient {

    private final ObjectMapper objectMapper;
    private final TickRepository tickRepository;

    public OkxWebSocketClientSpot(ObjectMapper objectMapper, TickRepository tickRepository) throws URISyntaxException {
        super(new URI("wss://ws.okx.com:8443/ws/v5/public"));
        this.objectMapper = objectMapper;
        this.tickRepository = tickRepository;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Соединение установлено");
        subscribe(); // Пример: подписка на свечные данные BTCUSDT с периодом 1 минута
    }

    @Override
    @SneakyThrows
    public void onMessage(String message) {
        System.out.println(message);
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
                    .side(Side.valueOf(operation.equals("sell") ? "Sell" : "Buy"))
                    .createDate(getDateTime(el.getTs()))
                    .symbol(Symbol.valueOf(symbol.replace("-USDT", "")))
                    .exchange("okx")
                    .instrument("spot")
                    .build();

            tickRepository.save(tick);
        });
    }

    @Override
    @SneakyThrows
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Соединение закрыто: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("Ошибка: " + ex.getMessage());
    }

    public void subscribe() {
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

        send(gson.toJson(request));
    }

}

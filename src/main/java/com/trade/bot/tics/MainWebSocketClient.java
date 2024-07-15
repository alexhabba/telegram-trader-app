package com.trade.bot.tics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.trade.bot.repository.TickRepository;
import lombok.SneakyThrows;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class MainWebSocketClient extends WebSocketClient {

    protected final TickRepository tickRepository;
    protected final ObjectMapper objectMapper;

    public MainWebSocketClient(String uri, TickRepository tickRepository, ObjectMapper objectMapper) throws URISyntaxException {
//        super(new URI("wss://ws.okx.com:8443/ws/v5/public")); spot
        super(new URI(uri));
        this.tickRepository = tickRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onOpen(ServerHandshake ServerHandshake) {
    }

    @Override
    @SneakyThrows
    public void onMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Соединение закрыто: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("Ошибка: " + ex.getMessage());
    }

    public LocalTime getTime(Long milliseconds) {
        return Instant.ofEpochMilli(milliseconds)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime().toLocalTime();
    }

    public LocalDateTime getDateTime(Long milliseconds) {
        return Instant.ofEpochMilli(milliseconds)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

}

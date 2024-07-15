package com.trade.bot.tics;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public class BybitWebSocketClient extends WebSocketClient {

    public BybitWebSocketClient() throws URISyntaxException {
//        super(new URI("wss://stream.bybit.com/spot/public/v3"));
        super(new URI("wss://ws.okx.com:8443/ws/v5/public"));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Соединение установлено");
        // Отправляем подписку на поток
        subscribe(); // Пример: подписка на свечные данные BTCUSDT с периодом 1 минута
    }

    ObjectMapper objectMapper = new ObjectMapper();
    @Override
    @SneakyThrows
    public void onMessage(String message) {
        // Обработка входящего сообщения
//        SpotTradeDto spotTradeDto = objectMapper.readValue(message, SpotTradeDto.class);
//
//        String q = spotTradeDto.getData().getQ();
//        String operation = spotTradeDto.getData().isM() ? "покупка" : "продажа";
//
//
//        double count = Double.parseDouble(q);
//
//        if (count > 1000) {
//            System.out.println(operation + "    " + q + "   " + spotTradeDto.getData().getP() + "  " + getTime(spotTradeDto.getData().getT()));
//        }
//        // ... (обработка данных)
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

    // Метод для отправки подписки
    private void subscribe() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("channel", "trades");
        jsonObject.addProperty("instType", "FUTURES");
        jsonObject.addProperty("instId", "BTC-USDT");

        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonObject);

        String strrr = "{\"channel\": \"tradess\", \"instType\": \"FUTURES\", \"instId\": \"WLD-USDT\"}";
        JSONObject request = new JSONObject();
        request.put("op", "subscribe");
//        request.put("args", new String[] {"trade.WLDUSDT"});
        request.put("args", new String[] {strrr});
        System.out.println(jsonString);
        send(createString());
    }

    public String createString() {
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

        // Преобразуем JSON-объект в строку JSON
        String jsonString = gson.toJson(request);

        // Выводим строку JSON
        System.out.println(jsonString);
        return jsonString;
    }

    // ... (добавьте другие методы для отправки запросов, например, для получения данных о позициях, заказах и т.д.)

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        // Замените на свои API ключи
        String apiKey = "YOUR_API_KEY";
        String secretKey = "YOUR_SECRET_KEY";

        BybitWebSocketClient client = new BybitWebSocketClient();
        client.connectBlocking();

        // ... (обработка данных)

        while (client.isOpen()) {
            Thread.sleep(100);
        }
        client.closeBlocking();
    }

    public static LocalTime getTime(Long milliseconds) {
        return Instant.ofEpochMilli(milliseconds)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime().toLocalTime();

    }

}

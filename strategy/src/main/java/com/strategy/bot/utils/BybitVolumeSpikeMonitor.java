package com.strategy.bot.utils;

import com.dao.bot.entity.Bar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.dao.bot.dto.KlineResult.getDateTime;
import static com.strategy.bot.utils.SymbolUtils.getLinearSymbols;
import static java.util.Objects.isNull;

/**
 * Мониторит всплески объёма по всем фьючерсным парам Bybit (USDT Perpetual)
 */
public class BybitVolumeSpikeMonitor {
    private static final String WS_URL = "wss://stream.bybit.com/v5/public/linear";
    private static final String SYMBOLS_URL = "https://api.bybit.com/v5/market/instruments-info?category=linear";
    private static final String INTERVAL = "1"; // 1 минута (без 'm')
    private static final int SMA_LENGTH = 21;
    private static final double MULTIPLIER = 5; // коэффициент всплеска
    private static final double MIN_VOLUME = 100000; // минимальный объём для сигнала

    private final OkHttpClient httpClient = new OkHttpClient();
    private final OkHttpClient wsClient = new OkHttpClient.Builder()
            .pingInterval(0, TimeUnit.SECONDS) // отключаем ping от okhttp
            .build();

    private final Gson gson = new Gson();
    private final Map<String, Deque<Double>> history = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> tempStorage = new ConcurrentHashMap<>();
    private final Map<String, Map<LocalDateTime, Bar>> historyBar = new LinkedHashMap<>();

    public static void main(String[] args) throws Exception {
        new BybitVolumeSpikeMonitor().start();
    }

    private void start() throws Exception {
        List<String> symbols = loadSymbols();
        System.out.printf("✅ Всего символов: %d%n", symbols.size());
        System.out.println("📥 Загружаем историю...");

        // Инициализация истории
        for (String s : symbols) history.put(s, new ArrayDeque<>());
        for (String s : symbols) historyBar.put(s, new HashMap<>());

        // Запуск потоков по 40 символов
        int batchSize = 40;
        for (int i = 0; i < symbols.size(); i += batchSize) {
            List<String> batch = symbols.subList(i, Math.min(symbols.size(), i + batchSize));
            connectWS(batch);
            Thread.sleep(1000);
        }

        // Мониторинг активности
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            // todo тут логика которую нужно запускать по расписанию
            tempStorage.entrySet().removeIf(entry -> entry.getValue().isAfter(LocalDateTime.now().minusMonths(3).minusMinutes(2)));
        }, 1, 60, TimeUnit.SECONDS);
    }

    private List<String> loadSymbols() throws IOException {
        List<String> linearSymbols = getLinearSymbols();
        Request req = new Request.Builder().url(SYMBOLS_URL).build();
        try (Response res = httpClient.newCall(req).execute()) {
            if (!res.isSuccessful()) {
                throw new IOException("Ошибка при загрузке символов: " + res);
            }
            JsonObject root = gson.fromJson(res.body().string(), JsonObject.class);
            JsonArray list = root.getAsJsonObject("result").getAsJsonArray("list");

            List<String> symbols = new ArrayList<>();
            for (JsonElement el : list) {
                JsonObject obj = el.getAsJsonObject();
                if (obj.has("symbol")) {
                    symbols.add(obj.get("symbol").getAsString());
                }
            }
            linearSymbols.stream()
                    .sorted()
                    .forEach(System.out::println);
            return linearSymbols;
        }
    }

    private void connectWS(List<String> symbols) {
        Request request = new Request.Builder().url(WS_URL).build();

        wsClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                List<String> args = new ArrayList<>();
                for (String s : symbols) args.add("kline." + INTERVAL + "." + s);

                JsonObject sub = new JsonObject();
                sub.addProperty("op", "subscribe");
                sub.add("args", gson.toJsonTree(args));

                webSocket.send(gson.toJson(sub));
                System.out.printf("🔗 Подключено (%d пар)%n", symbols.size());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                handleMessage(webSocket, text);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                System.err.println("⚠️ WS ошибка: " + t.getMessage());
                // Авто-переподключение через 5 секунд
                Executors.newSingleThreadScheduledExecutor().schedule(
                        () -> connectWS(symbols), 5, TimeUnit.SECONDS);
            }
        });
    }

    private void handleMessage(WebSocket ws, String text) {
        try {
            JsonObject root = gson.fromJson(text, JsonObject.class);
            if (root == null) return;

            // Bybit шлёт ping → отвечаем pong
            if (root.has("op") && "ping".equals(root.get("op").getAsString())) {
                JsonObject pong = new JsonObject();
                pong.addProperty("op", "pong");
                ws.send(gson.toJson(pong));
                return;
            }

            if (!root.has("topic")) return;

            String topic = root.get("topic").getAsString();

            if (topic.startsWith("kline.")) {
                JsonArray data = root.getAsJsonArray("data");
                if (data == null || data.isEmpty()) return;

                for (JsonElement e : data) {
                    JsonObject o = e.getAsJsonObject();
                    String symbol = topic.replace("kline.1.", "");
                    double volume = o.get("volume").getAsDouble();
                    long ts = o.get("end").getAsLong();
//                    update(symbol, volume, ts);
                    Bar bar = Bar.builder()
                            .symb(symbol)
                            .close(o.get("close").getAsDouble())
                            .open(o.get("open").getAsDouble())
                            .high(o.get("high").getAsDouble())
                            .low(o.get("low").getAsDouble())
                            .createDate(getDateTime(ts))
                            .vol(volume)
                            .build();
                    updateBar(bar);
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Ошибка парсинга: " + e.getMessage());
        }
    }

    private void update(String symbol, double vol, long ts) {
        Deque<Double> dq = history.get(symbol);
        if (dq == null) return;

        double sma = dq.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        if (dq.size() >= SMA_LENGTH) dq.removeFirst();
        dq.addLast(vol);

        boolean spike = sma > 0 && vol > MULTIPLIER * sma && vol > MIN_VOLUME;
        if (spike) {
//            System.out.printf("🚀 Всплеск [%s]: https://www.bybit.com/trade/usdt/%s %.2f (SMA %.2f) %s%n",
            System.out.printf("🚀 Всплеск [%s]: %.2f (SMA %.2f) " + "    https://www.bybit.com/trade/usdt/" + symbol + "  %s%n",
                    symbol, vol, sma, Instant.ofEpochMilli(ts));
        }
    }

    private void updateBar(Bar bar) {
        Map<LocalDateTime, Bar> timeBarMap = historyBar.get(bar.getSymb());

        if (timeBarMap.size() >= SMA_LENGTH) {
            double sma = timeBarMap.values().stream().mapToDouble(Bar::getVol).average().orElse(0);
            boolean spike = sma > 0 && bar.getVol() > MULTIPLIER * sma && bar.getVol() > MIN_VOLUME;

            // todo low * vol = usdt
            if (spike) {
                double volUsdt = bar.getVol() * bar.getLow();
                if (volUsdt > 100000  && isNull(tempStorage.get(bar.getSymb()))) {
                    tempStorage.put(bar.getSymb(), bar.getCreateDate());
                    System.out.printf("🚀 Всплеск [%s]: %.2f (SMA %.2f) " + "    https://www.bybit.com/trade/usdt/" + bar.getSymb() + "  %s        %s%n",
                            bar.getSymb(), bar.getVol(), sma, bar.getCreateDate(), LocalDateTime.now());
                }
            }

            timeBarMap.remove(bar.getCreateDate().minusMinutes(SMA_LENGTH));
        }
        timeBarMap.put(bar.getCreateDate(), bar);
    }
}



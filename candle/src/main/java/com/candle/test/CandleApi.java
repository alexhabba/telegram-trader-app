package com.candle.test;

import com.dao.bot.entity.Bar;
import com.dao.bot.entity.Tick;
import com.dao.bot.enums.Side;
import com.dao.bot.enums.Symbol;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.candle.test.BinanceTicks.getTicks;

public class CandleApi {

    public static List<Bar> getCandle(Symbol symbol, LocalDateTime startDateTime) throws IOException {
        String interval = "1m"; // 1m, 3m, 5m, 15m, 30m, 1h, 2h, 4h, 6h, 8h, 12h, 1d, 3d, 1w, 1M
        long startTime = startDateTime.toInstant(ZoneOffset.UTC).toEpochMilli(); // Начальное время в миллисекундах (Unix timestamp)
//        long endTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC); // Начальное время в миллисекундах (Unix timestamp)
        int limit = 1000;


//        String url = String.format("https://api.binance.com/api/v3/klines?symbol=%s&interval=%s&startTime=%d&endTime=%d&limit=%d",
        String url = String.format("https://api.binance.com/api/v3/klines?symbol=%s&interval=%s&limit=%d&startTime=%d",
                URLEncoder.encode(symbol + "USDT", StandardCharsets.UTF_8),
                interval, limit, startTime);

        String response = getResponse(url);
        List<Bar> bars = new ArrayList<>(1000);
        if (response != null) {
            try {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray candle = jsonArray.getJSONArray(i);
                    LocalDateTime dateTime = getDateTime(Long.parseLong(candle.get(0).toString()));
                    List<Tick> ticks = new ArrayList<>(1000);
                    while (true) {
                        List<Tick> list = getTicks(dateTime, dateTime.plusMinutes(1), symbol);
                        ticks.addAll(list);
                        if (list.size() != 1000) {
                            break;
                        }
                        if (ticks.size() > 13000) {
                            break;
                        }
                    }


                    double sumBuy = ticks.stream()
                            .filter(t -> t.getSide() == Side.Buy)
                            .mapToDouble(t -> Double.parseDouble(t.getQuantity()))
                            .sum();

                    double sumSell = ticks.stream()
                            .filter(t -> t.getSide() == Side.Sell)
                            .mapToDouble(t -> Double.parseDouble(t.getQuantity()))
                            .sum();

                    Bar bar = Bar.builder()
                            .createDate(dateTime)
                            .open(Double.parseDouble(candle.get(1).toString()))
                            .high(Double.parseDouble(candle.get(2).toString()))
                            .low(Double.parseDouble(candle.get(3).toString()))
                            .close(Double.parseDouble(candle.get(4).toString()))
                            .volBuy(sumBuy)
                            .volSell(sumSell)
                            .vol(sumSell + sumBuy)
                            .interval(1)
                            .symbol(symbol)
                            .build();
                    if (ticks.size() < 13000) {
                        bars.add(bar);
                    }
//                    System.out.println(i + "    " + bar);
                }

            } catch (Exception e) {
                System.out.println("Error parsing response: " + e.getMessage());
            }
        } else {
            System.out.println("No response from server");
        }
        return bars;
    }

    private static String getResponse(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                System.err.println("GET request failed with code " + response.code());
                return null;
            }
        }
    }

    public static LocalDateTime getDateTime(Long milliseconds) {
        return Instant.ofEpochMilli(milliseconds)
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime();
    }
}


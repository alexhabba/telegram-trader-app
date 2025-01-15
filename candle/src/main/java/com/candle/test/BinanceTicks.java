package com.candle.test;

import com.dao.bot.dto.SpotTradeDto;
import com.dao.bot.entity.Tick;
import com.dao.bot.enums.Side;
import com.dao.bot.enums.Symbol;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class BinanceTicks {

    private static final String API_KEY = "YOUR_API_KEY"; // Замените на ваш ключ
    private static final String API_SECRET = "YOUR_API_SECRET"; // Замените на ваш секрет

    public static void main(String[] args) throws IOException {
        String symbol = "WLDUSDT";
        long startTime = LocalDateTime.parse("2024-08-21T23:00:00").toInstant(ZoneOffset.UTC).toEpochMilli();
        long endTime = LocalDateTime.parse("2024-08-21T23:13:00").toInstant(ZoneOffset.UTC).toEpochMilli();
        int limit = 1000; // Максимальное количество свечей (максимум 1000)

        List<Tick> ticks = getTicks(symbol, startTime, endTime, limit);

        assert ticks != null;
//        ticks.forEach(System.out::println);
//        System.out.println(ticks.size());

    }

    public static List<Tick> getTicks(LocalDateTime start, LocalDateTime end, Symbol symbol) throws IOException {
        long startTime = start.toInstant(ZoneOffset.UTC).toEpochMilli();
        long endTime = end.toInstant(ZoneOffset.UTC).toEpochMilli();
        int limit = 1000; // Максимальное количество свечей (максимум 1000)

        List<Tick> ticks = getTicks(symbol + "USDT", startTime, endTime, limit);
        return ticks;

    }

    private static List<Tick> getTicks(
            String symbol, long startTime, long endTime, int limit)
            throws IOException {
//        System.out.println("sss " + startTime);
        ObjectMapper objectMapper = new ObjectMapper();

        String url =
                String.format(
                        "https://api.binance.com/api/v3/aggTrades?symbol=%s&startTime=%d&endTime=%d&limit=%d",
                        URLEncoder.encode(symbol, StandardCharsets.UTF_8), startTime, endTime, limit);
//                        "https://api.binance.com/api/v3/aggTrades?symbol=%s",
//                        URLEncoder.encode(symbol, StandardCharsets.UTF_8));

        // 1736065689462
        // 1736067600000
        String response = getResponse(url);

        if (response != null) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                List<Tick> ticks = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    SpotTradeDto trade = objectMapper.readValue(jsonArray.get(i).toString(), SpotTradeDto.class);
//                    System.out.println(trade);

                    String q = Double.valueOf(trade.getQuantity()).toString();
                    String p = Double.valueOf(trade.getPrice()).toString();

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
                return ticks;
            } catch (Exception e) {
                System.out.println("Error parsing response: " + e.getMessage());
                return null;
            }
        } else {
            System.out.println("No response from server");
            return null;
        }
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


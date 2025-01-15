package com.strategy.bot.startegy.test;

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

import com.dao.bot.entity.Tick;
import com.dao.bot.enums.Side;
import com.dao.bot.enums.Symbol;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

public class BinanceTicks {

    private static final String API_KEY = "YOUR_API_KEY"; // Замените на ваш ключ
    private static final String API_SECRET = "YOUR_API_SECRET"; // Замените на ваш секрет

    public static void main(String[] args) throws IOException {
        String symbol = "WLDUSDT";
        String interval = "1m"; // 1m, 3m, 5m, 15m, 30m, 1h, 2h, 4h, 6h, 8h, 12h, 1d, 3d, 1w, 1M
        long startTime = LocalDateTime.parse("2025-01-06T20:00:00").toInstant(ZoneOffset.UTC).toEpochMilli();
        long endTime = LocalDateTime.parse("2025-01-06T20:01:00").toInstant(ZoneOffset.UTC).toEpochMilli();
        int limit = 1000; // Максимальное количество свечей (максимум 1000)

        List<Tick> ticks = getTicks(symbol, interval, startTime, endTime, limit);
        double sum = ticks.stream()
                .mapToDouble(t -> Double.parseDouble(t.getQuantity()))
                .sum();

        double sumBuy = ticks.stream()
                .filter(t -> t.getSide() == Side.Buy)
                .mapToDouble(t -> Double.parseDouble(t.getQuantity()))
                .sum();

        double sumSell = ticks.stream()
                .filter(t -> t.getSide() == Side.Sell)
                .mapToDouble(t -> Double.parseDouble(t.getQuantity()))
                .sum();

        assert ticks != null;
        ticks.forEach(System.out::println);
        System.out.println(ticks.size());

        System.out.println(sum);
        System.out.println(sumBuy);
        System.out.println(sumSell);
        System.out.println(sumSell + sumBuy);

    }

    private static List<Tick> getTicks(
            String symbol, String interval, long startTime, long endTime, int limit)
            throws IOException {
        System.out.println("sss " + startTime);
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
//                    JSONArray candle = jsonArray.getJSONArray(i);
                    SpotTradeDto trade = objectMapper.readValue(jsonArray.get(i).toString(), SpotTradeDto.class);
                    System.out.println(trade);

                    String q = Double.valueOf(trade.getQuantity()).toString();
                    String p = Double.valueOf(trade.getPrice()).toString();
//                    String symbol = trade.getSymbol();

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
//                    ticks.add(tick);
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
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");
        int responseCode = con.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            System.err.println("GET request failed with code " + responseCode);
            return null;
        }
    }

    public static LocalDateTime getDateTime(Long milliseconds) {
        return Instant.ofEpochMilli(milliseconds)
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime();
    }
}


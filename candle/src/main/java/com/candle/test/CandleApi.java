package com.candle.test;

import com.candle.BookInfoDto;
import com.candle.book.BinanceSymbolsFetcher;
import com.dao.bot.entity.Bar;
import com.dao.bot.entity.Candle;
import com.dao.bot.entity.Tick;
import com.dao.bot.enums.Side;
import com.dao.bot.enums.Symbol;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONArray;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.candle.test.BinanceTicks.getTicks;
import static com.candle.test.BookServiceBinance.getInfoBookBySymbol;
import static com.candle.test.Utils.getResponse;

public class CandleApi {

    public static List<Bar> getCandle(Symbol symbol, LocalDateTime startDateTime) {
        String interval = "1m"; // 1m, 3m, 5m, 15m, 30m, 1h, 2h, 4h, 6h, 8h, 12h, 1d, 3d, 1w, 1M
        long startTime = startDateTime.toInstant(ZoneOffset.UTC).toEpochMilli(); // Начальное время в миллисекундах (Unix timestamp)
//        long endTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC); // Начальное время в миллисекундах (Unix timestamp)
        int limit = 1000;


//        String url = String.format("https://api.binance.com/api/v3/klines?symbol=%s&interval=%s&startTime=%d&endTime=%d&limit=%d",
        String url = String.format("https://api.binance.com/api/v3/klines?symbol=%s&interval=%s&limit=%d&startTime=%d",
                URLEncoder.encode(symbol + "USDT", StandardCharsets.UTF_8),
                interval, limit, startTime);

        String response = getResponse(url, 3);
        List<Bar> bars = new ArrayList<>(1000);
        if (response != null) {
            try {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray candle = jsonArray.getJSONArray(i);
                    LocalDateTime dateTime = getDateTime(Long.parseLong(candle.get(0).toString()));
                    List<Tick> ticks = new ArrayList<>(1000);
                    double volume = Double.parseDouble(candle.get(5).toString());

                    double sumBuy = 0;
                    double sumSell = 0;
                    while (true) {
                        List<Tick> list = getTicks(dateTime, dateTime.plusMinutes(1), symbol);
                        ticks.addAll(list);
                        if (list.size() != 1000) {
                            break;
                        }

                        sumBuy = ticks.stream()
                                .filter(t -> t.getSide() == Side.Buy)
                                .mapToDouble(t -> Double.parseDouble(t.getQuantity()))
                                .sum();

                        sumSell = ticks.stream()
                                .filter(t -> t.getSide() == Side.Sell)
                                .mapToDouble(t -> Double.parseDouble(t.getQuantity()))
                                .sum();
                        if (sumBuy + sumSell >= volume) {
                            break;
                        }
                    }

                    sumBuy = ticks.stream()
                            .filter(t -> t.getSide() == Side.Buy)
                            .mapToDouble(t -> Double.parseDouble(t.getQuantity()))
                            .sum();

                    sumSell = ticks.stream()
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
                            .vol(volume == 0 ? sumBuy + sumSell : volume)
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


    public static List<Candle> getCandleWithoutTicks(String symbol, LocalDateTime startDateTime, String interval) {
//        String interval = "5m"; // 1m, 3m, 5m, 15m, 30m, 1h, 2h, 4h, 6h, 8h, 12h, 1d, 3d, 1w, 1M
        long startTime = startDateTime.toInstant(ZoneOffset.UTC).toEpochMilli(); // Начальное время в миллисекундах (Unix timestamp)
//        long startTime = LocalDateTime.parse("2025-05-30T05:15:00").toInstant(ZoneOffset.UTC).toEpochMilli(); // Начальное время в миллисекундах (Unix timestamp)
//        long endTime = LocalDateTime.parse("2025-05-30T06:15:00").toInstant(ZoneOffset.UTC).toEpochMilli(); // Начальное время в миллисекундах (Unix timestamp)
        int limit = 1000;


//        String url = String.format("https://api.binance.com/api/v3/klines?symbol=%s&interval=%s&startTime=%d&endTime=%d&limit=%d",
//                symbol,
//                interval, startTime, endTime, limit);
//        String url = String.format("https://api.binance.com/api/v3/klines?symbol=%s&interval=%s&startTime=%d&endTime=%d&limit=%d",
        String url = String.format("https://api.binance.com/api/v3/klines?symbol=%s&interval=%s&limit=%d&startTime=%d",
                symbol,
                interval, limit, startTime);

        String response = getResponse(url, 3);
        List<Candle> candels = new ArrayList<>(1000);
        if (response != null) {
            try {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray candle = jsonArray.getJSONArray(i);
                    LocalDateTime dateTime = getDateTime(Long.parseLong(candle.get(0).toString()));
                    double volume = Double.parseDouble(candle.get(5).toString());

                    Candle c = Candle.builder()
                            .createDate(dateTime)
                            .open(Double.parseDouble(candle.get(1).toString()))
                            .high(Double.parseDouble(candle.get(2).toString()))
                            .low(Double.parseDouble(candle.get(3).toString()))
                            .close(Double.parseDouble(candle.get(4).toString()))
                            .vol(volume)
                            .interval(1)
                            .symbol(symbol)
                            .build();
                        candels.add(c);
                }

            } catch (Exception e) {
                System.out.println("Error parsing response: " + e.getMessage());
            }
        } else {
            System.out.println("No response from server");
        }
        return candels;
    }

    public static LocalDateTime getDateTime(Long milliseconds) {
        return Instant.ofEpochMilli(milliseconds)
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime();
    }
}


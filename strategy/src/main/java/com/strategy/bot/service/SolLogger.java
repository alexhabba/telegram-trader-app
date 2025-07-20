package com.strategy.bot.service;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SolLogger {

    private static final String LOG_FILE = FileSystems.getDefault().getPath("").toAbsolutePath() + "/sol.log";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Записывает данные о фьючерсе в лог-файл
     * @param symbol      Тикер фьючерса (BTCUSDT, ETHUSDT)
     * @param fundingRate Ставка финансирования
     * @param price        Ссылка на контракт
     */
    public static void logToFile(String symbol, double openInterestValue, double fundingRate, String price) {
        String logEntry = String.format("%-15s | %f | %f | %s | %s%n",
                symbol,
                openInterestValue,
                fundingRate * 100, // Конвертация в проценты
                LocalDateTime.now().format(TIME_FORMAT),
                price);


        try {
            // Создаем директории, если нужно
            Path path = Paths.get(LOG_FILE);
            if (!Files.exists(path)) {
//                Files.createDirectories(Path.of("/Users/alex/logs"));
                Files.createFile(Path.of(LOG_FILE));
            }

            // Записываем в файл (добавляем к существующему или создаем новый)
            Files.writeString(
                    path,
                    logEntry,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

        } catch (IOException e) {
            System.err.println("Ошибка записи в файл: " + e.getMessage());
        }
    }

    /**
     * Пример использования
     */
//    public static void main(String[] args) {
//        // Тестовые данные
//        List<String> symbols = List.of("BTCUSDT", "ETHUSDT", "SOLUSDT");
//
//        symbols.forEach(symbol -> {
//            double rate = -0.0085; // Пример ставки
//            String link = "https://www.bybit.com/trade/usdt/" + symbol;
//            logToFile(symbol, rate, link);
//        });
//    }
}
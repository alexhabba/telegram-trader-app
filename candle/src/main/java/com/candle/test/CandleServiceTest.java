package com.candle.test;

import com.candle.book.BinanceSymbolsFetcher;
import com.dao.bot.entity.Candle;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.candle.test.CandleApi.getCandleWithoutTicks;
import static java.lang.Math.max;

public class CandleServiceTest {
    private static final int medianCountTen = 10;
    private static final int medianCountTwenty = 20;

    private static double btcPrice;
    private static double ethPrice;
    private static double solPrice;

    private static final String BTC = "BTC";
    private static final String ETH = "ETH";
    private static final String SOL = "SOL";
    private static final String USDT = "USDT";

    private static final Map<Integer, Map<String, Integer>> map = Map.of(
            medianCountTen, Map.of(
                    "1d", (medianCountTen + 3) * 24 * 60,
                    "5m", (medianCountTen + 3) * 5
            ),
            medianCountTwenty, Map.of(
                    "1d", (medianCountTwenty + 3) * 24 * 60,
                    "5m", (medianCountTwenty + 3) * 5
            )
    );

    private static final Set<String> setSymbolExcludes = Set.of(
            "FDUSDUSDT",
            "USDCUSDT",
            "WBTCBTC",
            "XUSDUSDT",
            "TRXUSDT",
            "ETHBTC ",
            "ETHUSDT",
            "LTCUSDT",
            "XRPUSDT",
            "WBTCUSDT"
    );

    private static final Map<String, Function<Double, Integer>> REZOLVER = Map.of(
            BTC, CandleServiceTest::getVolBtc,
            ETH, CandleServiceTest::getVolEth,
            USDT, CandleServiceTest::getVolUsdt,
            SOL, CandleServiceTest::getVolSol
    );

    public static void main(String[] args) {
        BinanceSymbolsFetcher binanceSymbolsFetcher = new BinanceSymbolsFetcher();
        List<String> quoteAssets = List.of("USDT", "BTC", "SOL");
        List<String> symbols = new ArrayList<>();
        quoteAssets.forEach(qa -> {
            symbols.addAll(binanceSymbolsFetcher.getAllSymbol(qa));
        });

        while (true) {
            mainn(symbols, 10, "5m");
            System.out.println("=================================================================");
            System.out.println(LocalDateTime.now());
//            mainn(symbols, 20, "5m");
        }
    }

    public static void mainn(List<String> symbols, int medianCount, String interval) {
        Integer minutes = map.get(medianCount).get(interval);
        symbols.stream()
                .filter(sym -> !setSymbolExcludes.contains(sym))
                .forEach(s -> {

                    List<Candle> candle = getCandleWithoutTicks(s, LocalDateTime.now().minusHours(3).minusMinutes(minutes), interval);
                    List<Candle> collect = candle.stream().limit(medianCount).collect(Collectors.toList());

                    if (BTC.concat(USDT).equals(s)) {
                        btcPrice = candle.get(candle.size() - 1).getClose();
                    } else if (ETH.concat(USDT).equals(s)) {
                        ethPrice = candle.get(candle.size() - 1).getClose();
                    } else if (SOL.concat(USDT).equals(s)) {
                        solPrice = candle.get(candle.size() - 1).getClose();
                    }

                    // нужно не считать последние 3 свечи
                    double medianVolume = collect.stream()
                            .mapToDouble(Candle::getVol)
                            .sum() / collect.size() * 2;

                    Map<Double, LocalDateTime> mapVolLocalDateTime = new HashMap<>();
                    double vol0 = candle.get(candle.size() - 1).getVol();
                    mapVolLocalDateTime.put(vol0, candle.get(candle.size() - 1).getCreateDate());
                    double vol1 = candle.get(candle.size() - 2).getVol();
                    mapVolLocalDateTime.put(vol1, candle.get(candle.size() - 2).getCreateDate());
                    double vol2 = candle.get(candle.size() - 3).getVol();
                    mapVolLocalDateTime.put(vol2, candle.get(candle.size() - 3).getCreateDate());

                    double pr0 = candle.get(candle.size() - 1).getClose();
                    double maxVol = (max(vol0, max(vol1, vol2)) * pr0);
                    double maxVoll = (max(vol0, max(vol1, vol2)));
                    LocalDateTime forMaxVol = mapVolLocalDateTime.get(maxVoll);
                    int maxVolInUsdt = 0;
                    if (s.endsWith(SOL)) {
                        maxVolInUsdt = REZOLVER.get(SOL).apply(maxVol);
                    } else if (s.endsWith(BTC)) {
                        maxVolInUsdt = REZOLVER.get(BTC).apply(maxVol);
                    } else if (s.endsWith(ETH)) {
                        maxVolInUsdt = REZOLVER.get(ETH).apply(maxVol);
                    } else if (s.endsWith(USDT)) {
                        maxVolInUsdt = REZOLVER.get(USDT).apply(maxVol);
                    }
                    int bigVol = 1000000;
                    boolean checkSum = maxVolInUsdt > bigVol;

                    if (candle.size() >= medianCount && checkSum && (medianVolume < vol0 || medianVolume < vol1 || medianVolume < vol2)) {
                        String symb = s.replace("USDT", "_USDT");
                        // TODO сделать дельту
                        // https://www.binance.com/ru/trade/ZEN_USDT?type=spot
//                         https://www.okx.com/ru/trade-spot/PEPE-USDT
                        String symbOkx = s.replace("USDT", "-USDT");

                        System.out.println("https://www.binance.com/ru/trade/" + symb + "?type=spot     medianCount = " + medianCount + "   maxVolInUsdt = " + maxVolInUsdt);
                        System.out.println("https://www.okx.com/ru/trade-spot/" + symbOkx + "     medianCount = " + medianCount + "   maxVolInUsdt = " + maxVolInUsdt);
                        System.out.println(forMaxVol);
                    }
                });
    }

    private static Integer getVolBtc(double maxVol) {
        return (int) (maxVol * btcPrice);
    }

    private static Integer getVolEth(double maxVol) {
        return (int) (maxVol * ethPrice);
    }

    private static Integer getVolUsdt(double maxVol) {
        return (int) (maxVol * 1);
    }

    private static Integer getVolSol(double maxVol) {
        return (int) (maxVol * solPrice);
    }
}

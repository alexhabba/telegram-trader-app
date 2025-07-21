package com.strategy.bot.indicator;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.VolumeIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.DoubleNum;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Определяет, находится ли рынок во флэте.
 */
public class FlatDetector {

    private final int windowSize = 13;

    public boolean isFlat(List<com.dao.bot.entity.Bar> bars) {
        BarSeries series = new BaseBarSeries("market");

        // Строим серию из цен
        for (var barEntity : bars) {
            ZonedDateTime now = ZonedDateTime.now();
            Bar bar = createBar(barEntity, now);
            series.addBar(bar);
        }

        // Индикаторы
        RSIIndicator rsi = new RSIIndicator(new ClosePriceIndicator(series), windowSize);
        ATRIndicator atr = new ATRIndicator(series, windowSize);
        SMAIndicator sma = new SMAIndicator(new ClosePriceIndicator(series), windowSize);
        VolumeIndicator vi = new VolumeIndicator(series, windowSize);

        int endIndex = series.getEndIndex();

        double currentRsi = rsi.getValue(endIndex).doubleValue();
        double currentAtr = atr.getValue(endIndex).doubleValue();
        double currentSma = sma.getValue(endIndex).doubleValue();
        double currentPrice = series.getBar(endIndex).getClosePrice().doubleValue();

        boolean rsiInFlatRange = currentRsi > 45 && currentRsi < 55;
        boolean priceNearSma = Math.abs(currentPrice - currentSma) / currentSma < 0.002;
        boolean lowAtr = currentAtr < 1;

        return rsiInFlatRange && priceNearSma && lowAtr;
    }

    public static Bar createBar(com.dao.bot.entity.Bar bar, ZonedDateTime time) {
        return BaseBar.builder()
                .timePeriod(Duration.ofMinutes(1))
                .endTime(bar.getCreateDate().atZone(ZoneId.of("Europe/Moscow")))
                .openPrice(DecimalNum.valueOf(bar.getOpen()))
                .highPrice(DecimalNum.valueOf(bar.getHigh()))
                .lowPrice(DecimalNum.valueOf(bar.getLow()))
                .closePrice(DecimalNum.valueOf(bar.getClose()))
                .volume(DecimalNum.valueOf(bar.getVol()))
                .build();
    }

    public static void main(String[] args) {

        FlatDetector detector = new FlatDetector(); // 1% отклонения, SMA 5

        // Пример: почти неизменный рынок
//        boolean isFlat = detector.isFlat(Arrays.asList(100.0, 100.2, 100.1, 99.9, 100.05, 100.0));
//        System.out.println("Market is flat: " + isFlat);
//
//        // Пример: сильное движение
//        boolean isFlat2 = detector.isFlat(Arrays.asList(100.0, 100.5, 100.7, 101.0, 100.3, 100.0));
//        System.out.println("Market is flat: " + isFlat2);
//
//        List<Double> closePrices = generatePerfectFlatData(20, 100, 0.7);
//        boolean isFlat3 = detector.isFlat(closePrices);
//        System.out.println("Market is flat3: " + isFlat3);

    }

    private static List<Double> generatePerfectFlatData(int count, double base, double spread) {
        List<Double> data = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            data.add(base + (rand.nextDouble() * spread * 2) - spread);
        }
        return data;
    }
}


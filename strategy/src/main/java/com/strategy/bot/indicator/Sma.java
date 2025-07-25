package com.strategy.bot.indicator;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.MMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DecimalNum;

import java.time.Duration;
import java.time.ZoneId;
import java.util.List;

public class Sma {

    public static double getValue(List<com.dao.bot.entity.Bar> bars, int windowSize) {
        BarSeries series = new BaseBarSeries("market");

        // Строим серию из цен
        for (var barEntity : bars) {
            Bar bar = createBar(barEntity);
            series.addBar(bar);
        }
        MMAIndicator rsi = new MMAIndicator(new ClosePriceIndicator(series), windowSize);
        int endIndex = series.getEndIndex();

        return rsi.getValue(endIndex).doubleValue();
    }

    public static Bar createBar(com.dao.bot.entity.Bar bar) {
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
}

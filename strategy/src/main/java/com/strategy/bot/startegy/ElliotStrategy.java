package com.strategy.bot.startegy;

import com.dao.bot.entity.Bar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

//@Service
@RequiredArgsConstructor
public class ElliotStrategy implements StrategyExecutor {


    @Override
    public void execute(Bar lastBar, LocalDateTime lastDateTime) {

    }


    /**
     * Определение волны 3.
     *
     */
    public boolean isWave3Start(double wave1High, double wave2Low, double currentPrice, double volume, List<Bar> bars) {
        // Цена пробила максимум волны 1
        boolean priceCondition = currentPrice > wave1High;

        // Коррекция волны 2 была 50-61.8%
        double retracement = (wave1High - wave2Low) / wave1High;
        boolean fibCondition = retracement >= 0.5 && retracement <= 0.618;

        // Объем растет
        boolean volumeCondition = volume > calculateSMAVolume(bars, 25) * 1.5;

        return priceCondition && fibCondition && volumeCondition;
    }

    /**
     * Расчет среднего объема за последние N свечей.
     *
     * @param period Период: 20-50 свечей (на M15/H1)
     */
    public double calculateSMAVolume(List<Bar> bars, int period) {
        if (bars.size() < period) return 0.0;

        double sum = 0.0;
        for (int i = bars.size() - period; i < bars.size(); i++) {
            sum += bars.get(i).getVol();
        }
        return sum / period;
    }

}

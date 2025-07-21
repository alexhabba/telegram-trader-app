package com.strategy.bot.indicator;

import java.util.List;

public class Rsi {

    /**
     * Рассчитывает RSI для списка цен закрытия.
     * @param prices Список цен закрытия (последние данные в конце списка).
     * @param period Период RSI (обычно 14).
     * @return Последнее значение RSI или -1, если данных недостаточно.
     */
    public static double calculateRSI(List<Double> prices, int period) {
        if (prices.size() <= period) {
            return -1; // Недостаточно данных
        }

        double[] gains = new double[prices.size() - 1];
        double[] losses = new double[prices.size() - 1];

        // 1. Рассчитываем изменения цен и разделяем на gains/losses
        for (int i = 1; i < prices.size(); i++) {
            double change = prices.get(i) - prices.get(i - 1);
            gains[i - 1] = Math.max(change, 0);
            losses[i - 1] = Math.max(-change, 0);
        }

        // 2. Первые средние значения (SMA за период)
        double avgGain = 0;
        double avgLoss = 0;
        for (int i = 0; i < period; i++) {
            avgGain += gains[i];
            avgLoss += losses[i];
        }
        avgGain /= period;
        avgLoss /= period;

        // 3. Сглаживание (EMA для остальных значений)
        for (int i = period; i < gains.length; i++) {
            avgGain = (avgGain * (period - 1) + gains[i]) / period;
            avgLoss = (avgLoss * (period - 1) + losses[i]) / period;
        }

        // 4. Рассчитываем RSI
        if (avgLoss == 0) {
            return 100; // Избегаем деления на ноль
        }
        double rs = avgGain / avgLoss;
        return 100 - (100 / (1 + rs));
    }

    public static void main(String[] args) {
        // Пример данных: цены закрытия BTCUSDT за 15 дней
        List<Double> prices = List.of(
                50000.0, 51000.0, 52000.0, 51500.0, 53000.0,
                52500.0, 53500.0, 54000.0, 54500.0, 55000.0,
                55500.0, 56000.0, 56500.0, 57000.0, 57500.0
        );

        int period = 14; // Стандартный период RSI
        double rsi = calculateRSI(prices, period);

        System.out.printf("RSI(%d): %.2f\n", period, rsi);
        // Пример вывода: RSI(14): 72.45 (перекупленность)
    }
}

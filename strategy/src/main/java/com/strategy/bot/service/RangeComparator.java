package com.strategy.bot.service;

import java.util.Arrays;

public class RangeComparator {

    // Вычисляет допустимое отклонение в процентах от среднего значения
    public static double calculatePercentageRange(double[] numbers, double percentage) {
        if (numbers.length == 0) return 0;

        double average = Arrays.stream(numbers).average().orElse(0);
        double maxDeviation = Arrays.stream(numbers)
                .map(num -> Math.abs(num - average))
                .max()
                .orElse(0);

        return average * (percentage / 100); // Возвращаем допустимое отклонение
    }

    // Проверяет, находятся ли два числа в одном диапазоне с учетом % отклонения
    public static boolean areNumbersInRange(double num1, double num2) {
        double[] numbers = {num1, num2};
        double allowedDeviation = calculatePercentageRange(numbers, 0.07);

        double average = (num1 + num2) / 2;
        double minVal = average - allowedDeviation;
        double maxVal = average + allowedDeviation;

        return (num1 >= minVal && num1 <= maxVal) &&
                (num2 >= minVal && num2 <= maxVal);
    }

    public static void main(String[] args) {
//        double num1 = 161.7;
//        double num2 = 161.69;
        double num1 = 171.85;
        double num2 = 171.64;
        double allowedPercentage = 0.03; // 0.1% отклонение

        boolean isInRange = areNumbersInRange(num2, num1);
    }
}
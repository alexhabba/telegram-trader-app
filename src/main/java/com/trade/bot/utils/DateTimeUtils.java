package com.trade.bot.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class DateTimeUtils {

    public static LocalTime getTime(Long milliseconds) {
        return Instant.ofEpochMilli(milliseconds)
                .atZone(ZoneId.of("Europe/Moscow"))
                .toLocalDateTime().toLocalTime();
    }

    public static LocalDateTime getDateTime(Long milliseconds) {
        return Instant.ofEpochMilli(milliseconds)
                .atZone(ZoneId.of("Europe/Moscow"))
                .toLocalDateTime();
    }

    public static Long getDateTime(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public static void main(String[] args) {
        // trend
        long x1 = getDateTime(LocalDateTime.parse("2024-10-17T18:00:00"));
        long x2 = getDateTime(LocalDateTime.parse("2024-10-18T12:45:00"));

        double y1 = 2.075;
        double y2 = 2.137;

        long k1 = x2 - x1;
        double k2 = y2 - y1;

        double hypotenuse = Math.sqrt(k1 * k1 + k2 * k2);

        double angle1 = Math.toDegrees(Math.atan(k2 / k1));
        double angle2 = Math.toDegrees(Math.atan(k1 / k2));
        System.out.println(k1);
        System.out.println(k2);
        System.out.println(hypotenuse);
        System.out.println(angle1);
        System.out.println(angle2);
        System.out.println("Гипотенуза: " + hypotenuse);
        System.out.println("Угол 1: " + angle1 + " градусов");
        System.out.println("Угол 2: " + angle2 + " градусов");
        main2();
    }

    public static void main1(String[] args) {

        // Определение катетов
        double cathet1 = 931500000;
        double cathet2 = 0.887;

        // Вычисление гипотенузы
        double hypotenuse = Math.sqrt(cathet1 * cathet1 + cathet2 * cathet2);

        // Вычисление углов с использованием тригонометрических функций
        double angle1 = Math.toDegrees(Math.atan(cathet1 / cathet2)); // Угол между гипотенузой и катетом 2
        double angle2 = Math.toDegrees(Math.atan(cathet2 / cathet1)); // Угол между гипотенузой и катетом 1

        // Вывод результатов
        System.out.println("Гипотенуза: " + hypotenuse);
        System.out.println("Угол 1: " + angle1 + " градусов");
        System.out.println("Угол 2: " + angle2 + " градусов");
    }

    // получение 2 катета
    public static void main2() {
        System.out.println("main2");
        long x1 = getDateTime(LocalDateTime.parse("2024-10-17T18:00:00"));
        long x2 = getDateTime(LocalDateTime.parse("2024-10-21T03:00:00"));
        // Заданные значения
        double knownCathet = x2 - x1; // Известный катет
        double angle = 89.99999994737277; // Угол в градусах

        // Перевод угла в радианы
        double angleRadians = Math.toRadians(angle);

        // Вычисление второго катета
        double unknownCathet = knownCathet * Math.tan(Math.PI / 2 - angleRadians);

        System.out.println("Второй катет: " + unknownCathet);
    }
}

package com.strategy.bot.utils;

import java.time.LocalDateTime;

public class LocalDateTimeUtils {

    public static LocalDateTime getLocalDateTimeFromString(String str) {
       return LocalDateTime.parse(str);
    }
}

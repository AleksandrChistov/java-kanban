package ru.yandex.practicum.task.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeTaskUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static String format(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }

    public static LocalDateTime parse(String dateTime) {
        return LocalDateTime.parse(dateTime, formatter);
    }
}

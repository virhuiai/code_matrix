package com.virhuiai.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface DateUtils {

    default String formatDate(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    default long daysBetween(LocalDate start, LocalDate end) {
        return Math.abs(start.until(end).getDays());
    }

    default LocalDateTime now() {
        return LocalDateTime.now();
    }
}

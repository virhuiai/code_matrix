package com.jgoodies.common.internal;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/internal/DateUtils.class */
public final class DateUtils {
    private DateUtils() {
    }

    public static boolean isYesterday(Date date) {
        return isYesterday(toLocalDate(date));
    }

    public static boolean isYesterday(LocalDate date) {
        return LocalDate.now().minusDays(1L).equals(date);
    }

    public static boolean isToday(Date date) {
        return isToday(toLocalDate(date));
    }

    public static boolean isToday(LocalDate date) {
        return LocalDate.now().equals(date);
    }

    public static boolean isTomorrow(Date date) {
        return isTomorrow(toLocalDate(date));
    }

    public static boolean isTomorrow(LocalDate date) {
        return LocalDate.now().plusDays(1L).equals(date);
    }

    public static boolean isThisWeek(LocalDate date) {
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int thisWeek = LocalDate.now().get(woy);
        int dateWeek = date.get(woy);
        return thisWeek == dateWeek;
    }

    public static boolean isThisMonth(LocalDate date) {
        return LocalDate.now().getMonthValue() == date.getMonthValue();
    }

    public static boolean isThisYear(LocalDate date) {
        return LocalDate.now().getYear() == date.getYear();
    }

    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [java.time.ZonedDateTime] */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /* JADX WARN: Type inference failed for: r0v7, types: [java.time.LocalDateTime] */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        if (date instanceof Timestamp) {
            ((Timestamp) date).toLocalDateTime();
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}

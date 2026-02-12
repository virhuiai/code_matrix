package com.jgoodies.common.jsdl.internal;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.display.Displayable;
import com.jgoodies.common.internal.Messages;
import java.text.NumberFormat;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/internal/CommonFormats.class */
public class CommonFormats {
    public static final String COMMA_DELIMITER = ", ";
    public static final String DASH_DELIMITER = " – ";
    public static final String DOT_DELIMITER = " • ";
    public static final String SLASH_DELIMITER = " / ";
    protected static final Pattern NON_WORD_PATTERN = Pattern.compile("\\W*");
    protected static final Pattern WORD_PATTERN = Pattern.compile("\\w+\\W*");
    protected static final Pattern WHITESPACE_PATTERN = Pattern.compile("[^\\s\\u202f]*");
    public static final String NO_VALUE_POSSIBLE = "×";
    public static final String ZERO = "–";
    public static final String ALMOST_ZERO = "0";
    public static final String VALUE_UNKNOWN = "…";

    protected CommonFormats() {
    }

    public static String dashed(Object... items) {
        return joinWithDashes(items);
    }

    public static String dotted(Object... items) {
        return joinWithDots(items);
    }

    public static String slashed(Object... items) {
        return joinWithSlashes(items);
    }

    public static String join(CharSequence delimiter, Object... items) {
        Preconditions.checkNotBlank(delimiter, Messages.MUST_NOT_BE_BLANK, "delimiter");
        if (items == null || items.length == 0) {
            return "";
        }
        return join0(delimiter, (Stream<? extends Object>) Stream.of(items));
    }

    public static String join(CharSequence delimiter, List<? extends Object> items) {
        Preconditions.checkNotBlank(delimiter, Messages.MUST_NOT_BE_BLANK, "delimiter");
        if (items == null || items.size() == 0) {
            return "";
        }
        return join0(delimiter, items.stream());
    }

    public static String join(CharSequence delimiter, Stream<? extends Object> stream) {
        Preconditions.checkNotBlank(delimiter, Messages.MUST_NOT_BE_BLANK, "delimiter");
        return join0(delimiter, stream);
    }

    public static String joinWithCommas(Object... items) {
        return join0(COMMA_DELIMITER, items);
    }

    public static String joinWithCommas(List<? extends Object> items) {
        return join0(COMMA_DELIMITER, items);
    }

    public static String joinWithCommas(Stream<? extends Object> stream) {
        return join0(COMMA_DELIMITER, stream);
    }

    public static String joinWithDashes(Object... items) {
        return join0(DASH_DELIMITER, items);
    }

    public static String joinWithDashes(List<? extends Object> items) {
        return join0(DASH_DELIMITER, items);
    }

    public static String joinWithDashes(Stream<? extends Object> items) {
        return join0(DASH_DELIMITER, items);
    }

    public static String joinWithDots(Object... items) {
        return join0(DOT_DELIMITER, items);
    }

    public static String joinWithDots(List<? extends Object> items) {
        return join0(DOT_DELIMITER, items);
    }

    public static String joinWithDots(Stream<? extends Object> items) {
        return join0(DOT_DELIMITER, items);
    }

    public static String joinWithSlashes(Object... items) {
        return join0(SLASH_DELIMITER, items);
    }

    public static String joinWithSlashes(List<? extends Object> items) {
        return join0(SLASH_DELIMITER, items);
    }

    public static String joinWithSlashes(Stream<? extends Object> stream) {
        return join0(SLASH_DELIMITER, stream);
    }

    public static String formatNumber(long value) {
        return NumberFormat.getIntegerInstance().format(value);
    }

    public static String formatNumber(long value, String unit) {
        return formatNumber(formatNumber(value), unit);
    }

    public static String formatNumber(String number, String unit) {
        return number + (char) 8239 + unit;
    }

    public static String formatFromTo(Object from, Object to, Object... furtherLocations) {
        StringBuilder builder = new StringBuilder().append(from).append(" ‒ ").append(to);
        if (furtherLocations != null) {
            for (Object location : furtherLocations) {
                builder.append(" ‒ ").append(location);
            }
        }
        return builder.toString();
    }

    public static String formatObjectNo(Object object, String no) {
        return object + (char) 8239 + no;
    }

    public static String formatDisplayable(Displayable object) {
        return object == null ? "" : object.getDisplayString();
    }

    private static String join0(CharSequence delimiter, Object... items) {
        if (items == null || items.length == 0) {
            return "";
        }
        return join0(delimiter, (Stream<? extends Object>) Stream.of(items));
    }

    private static String join0(CharSequence delimiter, List<? extends Object> items) {
        if (items == null || items.size() == 0) {
            return "";
        }
        return join0(delimiter, items.stream());
    }

    private static String join0(CharSequence delimiter, Stream<? extends Object> stream) {
        return (String) stream.filter(item -> {
            return item != null;
        }).map((v0) -> {
            return v0.toString();
        }).filter((v0) -> {
            return Strings.isNotBlank(v0);
        }).collect(Collectors.joining(delimiter));
    }
}

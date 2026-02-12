package com.jgoodies.application;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.application.internal.Exceptions;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.DateUtils;
import com.jgoodies.common.internal.Messages;
import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/ResourceConverters.class */
public final class ResourceConverters {
    private static final List<ResourceConverter> CONVERTERS = new CopyOnWriteArrayList();
    private static final Map<Class<?>, ResourceConverter> CONVERTER_MAP = new ConcurrentHashMap();

    static {
        ResourceConverter defaultConverter = new DefaultConverter();
        register(defaultConverter);
    }

    private ResourceConverters() {
    }

    public static void register(ResourceConverter converter) {
        Preconditions.checkNotNull(converter, Messages.MUST_NOT_BE_NULL, "converter");
        CONVERTERS.add(0, converter);
        invalidateCache();
    }

    public static ResourceConverter forType(Class<?> type) {
        Preconditions.checkNotNull(type, Messages.MUST_NOT_BE_NULL, "type");
        ResourceConverter converter = CONVERTER_MAP.get(type);
        if (converter == null) {
            converter = lookUpConverter(type);
            CONVERTER_MAP.put(type, converter);
        }
        return converter;
    }

    private static ResourceConverter lookUpConverter(Class<?> type) {
        return CONVERTERS.stream().filter(converter -> {
            return converter.supportsType(type);
        }).findFirst().orElse(null);
    }

    private static void invalidateCache() {
        CONVERTER_MAP.clear();
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/ResourceConverters$DefaultConverter.class */
    private static final class DefaultConverter implements ResourceConverter {
        private static final List<Class<?>> SUPPORTED_TYPES = Arrays.asList(Boolean.class, Boolean.TYPE, Byte.class, Byte.TYPE, Calendar.class, Character.class, Character.TYPE, Color.class, Date.class, Dimension.class, Double.class, Double.TYPE, EmptyBorder.class, File.class, Float.class, Float.TYPE, Font.class, Icon.class, ImageIcon.class, Image.class, Insets.class, Integer.class, Integer.TYPE, KeyStroke.class, Long.class, Long.TYPE, LocalDate.class, MessageFormat.class, Point.class, Rectangle.class, Short.class, Short.TYPE, URI.class, URL.class);

        private DefaultConverter() {
        }

        static {
            KeyStroke.getKeyStroke("ENTER");
        }

        @Override // com.jgoodies.application.ResourceConverter
        public boolean supportsType(Class<?> type) {
            return SUPPORTED_TYPES.stream().anyMatch(supportedType -> {
                return supportedType == type;
            });
        }

        @Override // com.jgoodies.application.ResourceConverter
        public Object convert(ResourceMap r, String key, String value, Class<?> type) {
            if (type != Boolean.class) {
                try {
                    if (type != Boolean.TYPE) {
                        if (type == Byte.class || type == Byte.TYPE) {
                            return Byte.decode(value);
                        }
                        if (type == Calendar.class) {
                            return convertCalendar(key, value, r);
                        }
                        if (type == Character.class || type == Character.TYPE) {
                            if (value.length() != 1) {
                                throw new Exceptions.ResourceConversionException("Character value too long.", r, key, value);
                            }
                            return Character.valueOf(value.charAt(0));
                        }
                        if (type == Color.class) {
                            return convertColor(key, value, r);
                        }
                        if (type == Date.class) {
                            return convertDate(key, value, r);
                        }
                        if (type == Dimension.class) {
                            return convertDimension(key, value, r);
                        }
                        if (type == Double.class || type == Double.TYPE) {
                            return Double.valueOf(value);
                        }
                        if (type == EmptyBorder.class) {
                            return convertEmptyBorder(key, value, r);
                        }
                        if (type == File.class) {
                            return new File(value);
                        }
                        if (type == Float.class || type == Float.TYPE) {
                            return Float.valueOf(value);
                        }
                        if (type == Font.class) {
                            return convertFont(key, value, r);
                        }
                        if (type == Icon.class || type == ImageIcon.class) {
                            return convertImageIcon(key, value, r);
                        }
                        if (type == Image.class) {
                            return convertImage(key, value, r);
                        }
                        if (type == Insets.class) {
                            return convertInsets(key, value, r);
                        }
                        if (type == Integer.class || type == Integer.TYPE) {
                            return Integer.decode(value);
                        }
                        if (type == KeyStroke.class) {
                            return convertKeyStroke(key, value, r);
                        }
                        if (type == Long.class || type == Long.TYPE) {
                            return Long.decode(value);
                        }
                        if (type == LocalDate.class) {
                            return convertLocalDate(key, value, r);
                        }
                        if (type == MessageFormat.class) {
                            return new MessageFormat(value);
                        }
                        if (type == Point.class) {
                            return convertPoint(key, value, r);
                        }
                        if (type == Rectangle.class) {
                            return convertRectangle(key, value, r);
                        }
                        if (type == Short.class || type == Short.TYPE) {
                            return Short.decode(value);
                        }
                        if (type == URI.class) {
                            return convertURI(key, value, r);
                        }
                        if (type == URL.class) {
                            return convertURL(key, value, r);
                        }
                        throw new IllegalArgumentException("Unknown type " + type);
                    }
                } catch (Exceptions.ResourceConversionException e) {
                    throw e;
                } catch (Throwable t) {
                    throw new Exceptions.ResourceConversionException("Invalid " + type + " format.", r, key, value, t);
                }
            }
            return convertBoolean(key, value, r);
        }

        private static Boolean convertBoolean(String key, String value, ResourceMap r) {
            if (value.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            }
            if (value.equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            }
            if (value.equalsIgnoreCase("on") || value.equalsIgnoreCase("yes")) {
                return Boolean.TRUE;
            }
            if (value.equalsIgnoreCase("off") || value.equalsIgnoreCase("no")) {
                return Boolean.FALSE;
            }
            throw new Exceptions.ResourceConversionException("Illegal boolean format. Must be one of: true, false, yes, no, on, off", r, key, value);
        }

        private static Calendar convertCalendar(String key, String value, ResourceMap r) {
            Date date = convertDate(key, value, r);
            if (date == null) {
                return null;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        }

        private static Color convertColor(String key, String value, ResourceMap r) {
            String[] token = value.split(",");
            switch (token.length) {
                case 1:
                    if (value.startsWith("#")) {
                        return decodeAARRGGBBColor(key, value, r, 1);
                    }
                    if (value.startsWith("0x") || value.startsWith("0X")) {
                        return decodeAARRGGBBColor(key, value, r, 2);
                    }
                    throw new Exceptions.ResourceConversionException("Invalid Color format. Must be one of: AARRGGBB or R, G, B [, A].", r, key, value, null);
                case AnimatedLabel.LEFT /* 2 */:
                default:
                    throw new Exceptions.ResourceConversionException("Invalid Color format. Must be one of: AARRGGBB or R, G, B [, A].", r, key, value, null);
                case 3:
                case AnimatedLabel.RIGHT /* 4 */:
                    try {
                        int[] ints = parseInts(token);
                        return ints.length == 3 ? new Color(ints[0], ints[1], ints[2]) : new Color(ints[0], ints[1], ints[2], ints[3]);
                    } catch (NumberFormatException e) {
                        throw new Exceptions.ResourceConversionException("Invalid Color format. Must be one of: AARRGGBB or R, G, B [, A].", r, key, value, e);
                    }
            }
        }

        private static Color decodeAARRGGBBColor(String key, String value, ResourceMap r, int offset) {
            switch (value.length() - offset) {
                case 6:
                    return Color.decode(value);
                case AnimatedLabel.DEFAULT_FONT_EXTRA_SIZE /* 8 */:
                    return new Color(Long.decode(value).intValue(), true);
                default:
                    throw new Exceptions.ResourceConversionException("Invalid [AA]RRGGBB Color format.", r, key, value, null);
            }
        }

        private static Date convertDate(String key, String value, ResourceMap r) {
            try {
                Format mediumFormat = DateFormat.getDateInstance(2, Locale.ENGLISH);
                return (Date) mediumFormat.parseObject(value);
            } catch (ParseException e) {
                Format shortFormat = DateFormat.getDateInstance(3, Locale.ENGLISH);
                try {
                    return (Date) shortFormat.parseObject(value);
                } catch (Exception e2) {
                    throw new Exceptions.ResourceConversionException("Invalid date format.", r, key, value, e2);
                }
            }
        }

        private static LocalDate convertLocalDate(String key, String value, ResourceMap r) {
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException e) {
                return DateUtils.toLocalDate(convertDate(key, value, r));
            }
        }

        private static Dimension convertDimension(String key, String value, ResourceMap r) {
            try {
                int[] ints = decodeInts(value);
                if (ints.length != 2) {
                    throw new Exceptions.ResourceConversionException("Invalid Dimension format. Must be: <int>, <int>.", r, key, value, null);
                }
                return new Dimension(ints[0], ints[1]);
            } catch (NumberFormatException e) {
                throw new Exceptions.ResourceConversionException("Invalid Dimension format. Must be: <int>, <int>.", r, key, value, null);
            }
        }

        private static EmptyBorder convertEmptyBorder(String key, String value, ResourceMap r) {
            try {
                int[] ints = decodeInts(value);
                if (ints.length != 4) {
                    throw new Exceptions.ResourceConversionException("Invalid EmptyBorder format. Must be: <int>, <int>.", r, key, value, null);
                }
                return new EmptyBorder(ints[0], ints[1], ints[2], ints[3]);
            } catch (NumberFormatException e) {
                throw new Exceptions.ResourceConversionException("Invalid EmptyBorder format. Must be: <int>, <int>, <int>, <int>.", r, key, value, null);
            }
        }

        private static Font convertFont(String key, String value, ResourceMap r) {
            if (Strings.isBlank(value)) {
                throw new Exceptions.ResourceConversionException("Missing font description.", r, key, value, null);
            }
            int commaIndex = value.indexOf(44);
            if (commaIndex == -1) {
                return Font.decode(value);
            }
            String formatValue = value.substring(0, commaIndex).trim();
            String pathValue = value.substring(commaIndex + 1).trim();
            if (formatValue.equalsIgnoreCase("TrueType")) {
                return convertFontFromURL(key, pathValue, r, "TrueType font", 0);
            }
            if (formatValue.equalsIgnoreCase("Type1")) {
                return convertFontFromURL(key, pathValue, r, "Type1 font", 1);
            }
            throw new Exceptions.ResourceConversionException("Invalid font format name '" + formatValue + "'. Must be one of: TrueType, Type1", r, key, value, null);
        }

        private static Font convertFontFromURL(String key, String value, ResourceMap r, String fontFormatName, int fontFormat) {
            URL url = getURL(key, value, r, fontFormatName);
            try {
                InputStream in = url.openStream();
                Throwable th = null;
                try {
                    try {
                        Font createFont = Font.createFont(fontFormat, in);
                        if (in != null) {
                            if (0 != 0) {
                                try {
                                    in.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                in.close();
                            }
                        }
                        return createFont;
                    } finally {
                    }
                } catch (Throwable th3) {
                    if (in != null) {
                        if (th != null) {
                            try {
                                in.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            in.close();
                        }
                    }
                    throw th3;
                }
            } catch (FontFormatException e) {
                throw new Exceptions.ResourceConversionException("Invalid " + fontFormatName + " format.", r, key, value, e);
            } catch (IOException e2) {
                throw new Exceptions.ResourceConversionException("Invalid " + fontFormatName + " resource.", r, key, value, e2);
            }
        }

        private static Image convertImage(String key, String value, ResourceMap r) {
            URL url = getURL(key, value, r, "image");
            try {
                return ImageIO.read(url);
            } catch (IOException e) {
                throw new Exceptions.ResourceConversionException("Can't read image.", r, key, value, e);
            }
        }

        private static ImageIcon convertImageIcon(String key, String value, ResourceMap r) {
            URL url = getURL(key, value, r, "image icon");
            return new ImageIcon(url);
        }

        private static Insets convertInsets(String key, String value, ResourceMap r) {
            try {
                int[] ints = decodeInts(value);
                if (ints.length != 4) {
                    throw new Exceptions.ResourceConversionException("Invalid Insets format. Must be: <int>, <int>.", r, key, value, null);
                }
                return new Insets(ints[0], ints[1], ints[2], ints[3]);
            } catch (NumberFormatException e) {
                throw new Exceptions.ResourceConversionException("Invalid Insets format. Must be: <int>, <int>, <int>, <int>.", r, key, value, null);
            }
        }

        private static KeyStroke convertKeyStroke(String key, String value, ResourceMap r) {
            try {
                return AWTKeyStroke.getAWTKeyStroke(value);
            } catch (Exception e) {
                throw new Exceptions.ResourceConversionException("Invalid KeyStroke format.\nSee the cause below and KeyStroke#getKeyStroke for details.\nValid key stroke examples: ctrl A, shift ctrl B", r, key, value, e);
            }
        }

        private static Point convertPoint(String key, String value, ResourceMap r) {
            try {
                int[] ints = decodeInts(value);
                if (ints.length != 2) {
                    throw new Exceptions.ResourceConversionException("Invalid Point format. Must be: <int>, <int>.", r, key, value, null);
                }
                return new Point(ints[0], ints[1]);
            } catch (NumberFormatException e) {
                throw new Exceptions.ResourceConversionException("Invalid Point format. Must be: <int>, <int>.", r, key, value, null);
            }
        }

        private static Rectangle convertRectangle(String key, String value, ResourceMap r) {
            try {
                int[] ints = decodeInts(value);
                if (ints.length != 4) {
                    throw new Exceptions.ResourceConversionException("Invalid Rectangle format. Must be: <int>, <int>.", r, key, value, null);
                }
                return new Rectangle(ints[0], ints[1], ints[2], ints[3]);
            } catch (NumberFormatException e) {
                throw new Exceptions.ResourceConversionException("Invalid Rectangle format. Must be: <int>, <int>, <int>, <int>.", r, key, value, null);
            }
        }

        private static URI convertURI(String key, String value, ResourceMap r) {
            try {
                return new URI(value);
            } catch (URISyntaxException e) {
                throw new Exceptions.ResourceConversionException("Invalid URI syntax.", r, key, value, e);
            }
        }

        private static URL convertURL(String key, String value, ResourceMap r) {
            try {
                return new URL(value);
            } catch (MalformedURLException e) {
                String path = r.resolvePath(value);
                URL url = r.getClassLoader().getResource(path);
                if (url != null) {
                    return url;
                }
                throw new Exceptions.ResourceConversionException("Invalid URL.\npath=" + path, r, key, value, e);
            }
        }

        private static URL getURL(String key, String value, ResourceMap r, String objectType) {
            if (Strings.isBlank(value)) {
                throw new Exceptions.ResourceConversionException("Missing " + objectType + " path.", r, key, value, null);
            }
            String path = r.resolvePath(value);
            URL url = r.getClassLoader().getResource(path);
            if (url != null) {
                return url;
            }
            throw new Exceptions.ResourceConversionException("Invalid " + objectType + " path.\nParent path=" + r.getResourceParentPath(), r, key, value, null);
        }

        private static int[] decodeInts(String value) {
            String[] token = value.split(",");
            int[] result = new int[token.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = Integer.decode(token[i].trim()).intValue();
            }
            return result;
        }

        private static int[] parseInts(String[] token) {
            int[] result = new int[token.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = Integer.parseInt(token[i].trim());
            }
            return result;
        }
    }
}

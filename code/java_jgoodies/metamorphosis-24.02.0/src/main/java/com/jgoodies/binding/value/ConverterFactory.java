package com.jgoodies.binding.value;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.DateUtils;
import com.jgoodies.common.internal.Messages;
import java.text.Format;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/ConverterFactory.class */
public final class ConverterFactory {
    private ConverterFactory() {
    }

    public static ConverterValueModel<Boolean, Boolean> createBooleanNegator(ValueModel booleanSource) {
        return new ConverterValueModel<>(booleanSource, new BooleanNegator());
    }

    public static ConverterValueModel<Boolean, String> createBooleanToStringConverter(ValueModel booleanSubject, String trueText, String falseText) {
        return createBooleanToStringConverter(booleanSubject, trueText, falseText, "");
    }

    public static ConverterValueModel<Boolean, String> createBooleanToStringConverter(ValueModel booleanSource, String trueText, String falseText, String nullText) {
        return new ConverterValueModel<>(booleanSource, new BooleanToStringConverter(trueText, falseText, nullText));
    }

    public static ConverterValueModel<Double, Double> createDoubleConverter(ValueModel doubleSource, double multiplier) {
        return new ConverterValueModel<>(doubleSource, new DoubleConverter(multiplier));
    }

    public static ConverterValueModel<Double, Integer> createDoubleToIntegerConverter(ValueModel doubleSource) {
        return createDoubleToIntegerConverter(doubleSource, 1);
    }

    public static ConverterValueModel<Double, Integer> createDoubleToIntegerConverter(ValueModel doubleSource, int multiplier) {
        return new ConverterValueModel<>(doubleSource, new DoubleToIntegerConverter(multiplier));
    }

    public static ConverterValueModel<Float, Float> createFloatConverter(ValueModel floatSource, float multiplier) {
        return new ConverterValueModel<>(floatSource, new FloatConverter(multiplier));
    }

    public static ConverterValueModel<Float, Integer> createFloatToIntegerConverter(ValueModel floatSource) {
        return createFloatToIntegerConverter(floatSource, 1);
    }

    public static ConverterValueModel<Float, Integer> createFloatToIntegerConverter(ValueModel floatSource, int multiplier) {
        return new ConverterValueModel<>(floatSource, new FloatToIntegerConverter(multiplier));
    }

    public static ConverterValueModel<Integer, Integer> createIntegerConverter(ValueModel integerSource, double multiplier) {
        return new ConverterValueModel<>(integerSource, new IntegerConverter(multiplier));
    }

    public static ConverterValueModel<Integer, Double> createIntegerToDoubleConverter(ValueModel integerSource, double multiplier) {
        return new ConverterValueModel<>(integerSource, new IntegerToDoubleConverter(multiplier));
    }

    public static ConverterValueModel<Long, Long> createLongConverter(ValueModel longSource, double multiplier) {
        return new ConverterValueModel<>(longSource, new LongConverter(multiplier));
    }

    public static ConverterValueModel<Long, Integer> createLongToIntegerConverter(ValueModel longSubject) {
        return createLongToIntegerConverter(longSubject, 1);
    }

    public static ConverterValueModel<Long, Integer> createLongToIntegerConverter(ValueModel longSource, int multiplier) {
        return new ConverterValueModel<>(longSource, new LongToIntegerConverter(multiplier));
    }

    public static ConverterValueModel<Object, String> createStringConverter(ValueModel source, Format format) {
        return new ConverterValueModel<>(source, new StringConverter(format));
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/ConverterFactory$BooleanNegator.class */
    public static final class BooleanNegator implements BindingConverter<Boolean, Boolean> {
        @Override // com.jgoodies.binding.value.BindingConverter
        public Boolean targetValue(Boolean sourceValue) {
            return negate(sourceValue);
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Boolean sourceValue(Boolean targetValue) {
            return negate(targetValue);
        }

        private static Boolean negate(Boolean value) {
            if (value == null) {
                return null;
            }
            return Boolean.valueOf(!value.booleanValue());
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/ConverterFactory$BooleanToStringConverter.class */
    public static final class BooleanToStringConverter implements BindingConverter<Boolean, String> {
        private final String trueText;
        private final String falseText;
        private final String nullText;

        public BooleanToStringConverter(String trueText, String falseText, String nullText) {
            this.trueText = (String) Preconditions.checkNotNull(trueText, Messages.MUST_NOT_BE_NULL, "trueText");
            this.falseText = (String) Preconditions.checkNotNull(falseText, Messages.MUST_NOT_BE_NULL, "falseText");
            this.nullText = (String) Preconditions.checkNotNull(nullText, Messages.MUST_NOT_BE_NULL, "nullText");
            Preconditions.checkArgument(!trueText.equals(falseText), "The trueText and falseText must be different.");
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public String targetValue(Boolean sourceValue) {
            if (sourceValue == null) {
                return this.nullText;
            }
            return sourceValue.booleanValue() ? this.trueText : this.falseText;
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Boolean sourceValue(String targetValue) {
            if (this.trueText.equalsIgnoreCase(targetValue)) {
                return Boolean.TRUE;
            }
            if (this.falseText.equalsIgnoreCase(targetValue)) {
                return Boolean.FALSE;
            }
            if (this.nullText.equalsIgnoreCase(targetValue)) {
                return null;
            }
            throw new IllegalArgumentException("The new value must be one of: " + this.trueText + '/' + this.falseText + '/' + this.nullText);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/ConverterFactory$DoubleConverter.class */
    public static final class DoubleConverter implements BindingConverter<Double, Double> {
        private final double multiplier;

        public DoubleConverter(double multiplier) {
            this.multiplier = multiplier;
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Double targetValue(Double sourceValue) {
            return Double.valueOf(sourceValue.doubleValue() * this.multiplier);
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Double sourceValue(Double targetValue) {
            return Double.valueOf(targetValue.doubleValue() / this.multiplier);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/ConverterFactory$DoubleToIntegerConverter.class */
    public static final class DoubleToIntegerConverter implements BindingConverter<Double, Integer> {
        private final int multiplier;

        public DoubleToIntegerConverter(int multiplier) {
            this.multiplier = multiplier;
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Integer targetValue(Double sourceValue) {
            double doubleValue = sourceValue.doubleValue();
            if (this.multiplier != 1) {
                doubleValue *= this.multiplier;
            }
            return Integer.valueOf((int) Math.round(doubleValue));
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Double sourceValue(Integer targetValue) {
            double doubleValue = targetValue.doubleValue();
            if (this.multiplier != 1) {
                doubleValue /= this.multiplier;
            }
            return Double.valueOf(doubleValue);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/ConverterFactory$FloatConverter.class */
    public static final class FloatConverter implements BindingConverter<Float, Float> {
        private final float multiplier;

        public FloatConverter(float multiplier) {
            this.multiplier = multiplier;
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Float targetValue(Float sourceValue) {
            float floatValue = sourceValue.floatValue();
            return Float.valueOf(floatValue * this.multiplier);
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Float sourceValue(Float targetValue) {
            float floatValue = targetValue.floatValue();
            return Float.valueOf(floatValue / this.multiplier);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/ConverterFactory$FloatToIntegerConverter.class */
    public static final class FloatToIntegerConverter implements BindingConverter<Float, Integer> {
        private final int multiplier;

        public FloatToIntegerConverter(int multiplier) {
            this.multiplier = multiplier;
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Integer targetValue(Float sourceValue) {
            float floatValue = sourceValue.floatValue();
            if (this.multiplier != 1) {
                floatValue *= this.multiplier;
            }
            return Integer.valueOf(Math.round(floatValue));
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Float sourceValue(Integer sourceValue) {
            float floatValue = sourceValue.floatValue();
            if (this.multiplier != 1) {
                floatValue /= this.multiplier;
            }
            return Float.valueOf(floatValue);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/ConverterFactory$IntegerConverter.class */
    public static final class IntegerConverter implements BindingConverter<Integer, Integer> {
        private final double multiplier;

        public IntegerConverter(double multiplier) {
            this.multiplier = multiplier;
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Integer targetValue(Integer sourceValue) {
            double doubleValue = sourceValue.doubleValue();
            return Integer.valueOf((int) (doubleValue * this.multiplier));
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Integer sourceValue(Integer targetValue) {
            double doubleValue = targetValue.doubleValue();
            return Integer.valueOf((int) (doubleValue / this.multiplier));
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/ConverterFactory$IntegerToDoubleConverter.class */
    public static final class IntegerToDoubleConverter implements BindingConverter<Integer, Double> {
        private final double multiplier;

        public IntegerToDoubleConverter(double multiplier) {
            this.multiplier = multiplier;
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Double targetValue(Integer sourceValue) {
            int intValue = sourceValue.intValue();
            return Double.valueOf(intValue * this.multiplier);
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Integer sourceValue(Double targetValue) {
            double doubleValue = targetValue.doubleValue();
            return Integer.valueOf((int) (doubleValue / this.multiplier));
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/ConverterFactory$LocalDateToDateConverter.class */
    public static final class LocalDateToDateConverter implements BindingConverter<LocalDate, Date> {
        @Override // com.jgoodies.binding.value.BindingConverter
        public LocalDate sourceValue(Date targetValue) {
            return DateUtils.toLocalDate(targetValue);
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Date targetValue(LocalDate sourceValue) {
            return DateUtils.toDate(sourceValue);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/ConverterFactory$LocalDateTimeToDateConverter.class */
    public static final class LocalDateTimeToDateConverter implements BindingConverter<LocalDateTime, Date> {
        @Override // com.jgoodies.binding.value.BindingConverter
        public LocalDateTime sourceValue(Date targetValue) {
            return DateUtils.toLocalDateTime(targetValue);
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Date targetValue(LocalDateTime sourceValue) {
            return DateUtils.toDate(sourceValue);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/ConverterFactory$LongConverter.class */
    public static final class LongConverter implements BindingConverter<Long, Long> {
        private final double multiplier;

        public LongConverter(double multiplier) {
            this.multiplier = multiplier;
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Long targetValue(Long sourceValue) {
            double doubleValue = sourceValue.doubleValue();
            return Long.valueOf((long) (doubleValue * this.multiplier));
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Long sourceValue(Long targetValue) {
            double doubleValue = targetValue.doubleValue();
            return Long.valueOf((long) (doubleValue / this.multiplier));
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/ConverterFactory$LongToIntegerConverter.class */
    public static final class LongToIntegerConverter implements BindingConverter<Long, Integer> {
        private final int multiplier;

        public LongToIntegerConverter(int multiplier) {
            this.multiplier = multiplier;
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Integer targetValue(Long sourceValue) {
            int intValue = sourceValue.intValue();
            if (this.multiplier != 1) {
                intValue *= this.multiplier;
            }
            return Integer.valueOf(intValue);
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Long sourceValue(Integer targetValue) {
            long longValue = targetValue.longValue();
            if (this.multiplier != 1) {
                longValue /= this.multiplier;
            }
            return Long.valueOf(longValue);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/ConverterFactory$StringConverter.class */
    public static final class StringConverter implements BindingConverter<Object, String> {
        private final Format format;

        public StringConverter(Format format) {
            this.format = (Format) Preconditions.checkNotNull(format, Messages.MUST_NOT_BE_NULL, "format");
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.jgoodies.binding.value.BindingConverter
        public String targetValue(Object sourceValue) {
            return this.format.format(sourceValue);
        }

        @Override // com.jgoodies.binding.value.BindingConverter
        public Object sourceValue(String targetValue) {
            try {
                return this.format.parseObject(targetValue);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Cannot parse the target value " + targetValue, e);
            }
        }
    }
}

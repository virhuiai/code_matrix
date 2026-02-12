package com.jgoodies.common.format;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Objects;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/format/EmptyNumberFormat.class */
public final class EmptyNumberFormat extends NumberFormat {
    private final NumberFormat delegate;
    private final Number emptyValue;

    public EmptyNumberFormat(NumberFormat delegate) {
        this(delegate, (Number) null);
    }

    public EmptyNumberFormat(NumberFormat delegate, int emptyValue) {
        this(delegate, Integer.valueOf(emptyValue));
    }

    public EmptyNumberFormat(NumberFormat delegate, Number emptyValue) {
        this.delegate = (NumberFormat) Preconditions.checkNotNull(delegate, Messages.MUST_NOT_BE_NULL, "delegate format");
        this.emptyValue = emptyValue;
    }

    @Override // java.text.NumberFormat, java.text.Format
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        return Objects.equals(obj, this.emptyValue) ? toAppendTo : this.delegate.format(obj, toAppendTo, pos);
    }

    @Override // java.text.NumberFormat
    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
        return this.delegate.format(number, toAppendTo, pos);
    }

    @Override // java.text.NumberFormat
    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
        return this.delegate.format(number, toAppendTo, pos);
    }

    @Override // java.text.Format
    public Object parseObject(String source) throws ParseException {
        return Strings.isBlank(source) ? this.emptyValue : super.parseObject(source);
    }

    @Override // java.text.NumberFormat
    public Number parse(String source) throws ParseException {
        return Strings.isBlank(source) ? this.emptyValue : super.parse(source);
    }

    @Override // java.text.NumberFormat
    public Number parse(String source, ParsePosition pos) {
        return this.delegate.parse(source, pos);
    }
}

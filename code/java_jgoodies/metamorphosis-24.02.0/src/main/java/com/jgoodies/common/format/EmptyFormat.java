package com.jgoodies.common.format;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import java.text.AttributedCharacterIterator;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Objects;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/format/EmptyFormat.class */
public class EmptyFormat extends Format {
    private final Format delegate;
    private final Object emptyValue;

    public EmptyFormat(Format delegate) {
        this(delegate, null);
    }

    public EmptyFormat(Format delegate, Object emptyValue) {
        this.delegate = (Format) Preconditions.checkNotNull(delegate, Messages.MUST_NOT_BE_NULL, "delegate format");
        this.emptyValue = emptyValue;
    }

    @Override // java.text.Format
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        return Objects.equals(obj, this.emptyValue) ? toAppendTo : this.delegate.format(obj, toAppendTo, pos);
    }

    @Override // java.text.Format
    public Object parseObject(String source) throws ParseException {
        return Strings.isBlank(source) ? this.emptyValue : super.parseObject(source);
    }

    @Override // java.text.Format
    public final Object parseObject(String source, ParsePosition pos) {
        return this.delegate.parseObject(source, pos);
    }

    @Override // java.text.Format
    public final AttributedCharacterIterator formatToCharacterIterator(Object obj) {
        return this.delegate.formatToCharacterIterator(obj);
    }
}

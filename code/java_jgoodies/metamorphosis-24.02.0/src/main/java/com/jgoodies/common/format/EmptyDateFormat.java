package com.jgoodies.common.format;

import com.jgoodies.common.base.Strings;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Objects;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/format/EmptyDateFormat.class */
public final class EmptyDateFormat extends AbstractWrappedDateFormat {
    private final Date emptyValue;

    public EmptyDateFormat(DateFormat delegate) {
        this(delegate, null);
    }

    public EmptyDateFormat(DateFormat delegate, Date emptyValue) {
        super(delegate);
        this.emptyValue = emptyValue;
    }

    @Override // com.jgoodies.common.format.AbstractWrappedDateFormat, java.text.DateFormat
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
        return Objects.equals(date, this.emptyValue) ? toAppendTo : this.delegate.format(date, toAppendTo, pos);
    }

    @Override // com.jgoodies.common.format.AbstractWrappedDateFormat, java.text.DateFormat
    public Date parse(String source, ParsePosition pos) {
        if (Strings.isBlank(source)) {
            pos.setIndex(1);
            return this.emptyValue;
        }
        return this.delegate.parse(source, pos);
    }
}

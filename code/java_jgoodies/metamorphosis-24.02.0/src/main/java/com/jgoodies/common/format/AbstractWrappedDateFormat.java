package com.jgoodies.common.format;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.text.AttributedCharacterIterator;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/format/AbstractWrappedDateFormat.class */
public abstract class AbstractWrappedDateFormat extends DateFormat {
    protected final DateFormat delegate;

    @Override // java.text.DateFormat
    public abstract StringBuffer format(Date date, StringBuffer stringBuffer, FieldPosition fieldPosition);

    @Override // java.text.DateFormat
    public abstract Date parse(String str, ParsePosition parsePosition);

    public AbstractWrappedDateFormat(DateFormat delegate) {
        this.delegate = (DateFormat) Preconditions.checkNotNull(delegate, Messages.MUST_NOT_BE_NULL, "delegate format");
    }

    @Override // java.text.DateFormat
    public Calendar getCalendar() {
        return this.delegate.getCalendar();
    }

    @Override // java.text.DateFormat
    public void setCalendar(Calendar newCalendar) {
        this.delegate.setCalendar(newCalendar);
    }

    @Override // java.text.DateFormat
    public NumberFormat getNumberFormat() {
        return this.delegate.getNumberFormat();
    }

    @Override // java.text.DateFormat
    public void setNumberFormat(NumberFormat newNumberFormat) {
        this.delegate.setNumberFormat(newNumberFormat);
    }

    @Override // java.text.DateFormat
    public TimeZone getTimeZone() {
        return this.delegate.getTimeZone();
    }

    @Override // java.text.DateFormat
    public void setTimeZone(TimeZone zone) {
        this.delegate.setTimeZone(zone);
    }

    @Override // java.text.DateFormat
    public boolean isLenient() {
        return this.delegate.isLenient();
    }

    @Override // java.text.DateFormat
    public void setLenient(boolean lenient) {
        this.delegate.setLenient(lenient);
    }

    @Override // java.text.Format
    public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
        return this.delegate.formatToCharacterIterator(obj);
    }
}

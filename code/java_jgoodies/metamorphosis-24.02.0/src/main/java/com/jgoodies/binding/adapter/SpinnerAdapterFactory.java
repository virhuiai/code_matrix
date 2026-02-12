package com.jgoodies.binding.adapter;

import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.util.Date;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/SpinnerAdapterFactory.class */
public final class SpinnerAdapterFactory {
    private SpinnerAdapterFactory() {
    }

    public static SpinnerDateModel createDateAdapter(ValueModel valueModel, Date defaultDate) {
        return createDateAdapter(valueModel, defaultDate, null, null, 5);
    }

    public static SpinnerDateModel createDateAdapter(ValueModel valueModel, Date defaultDate, Comparable<Date> start, Comparable<Date> end, int calendarField) {
        Preconditions.checkNotNull(valueModel, Messages.MUST_NOT_BE_NULL, "valueModel");
        Preconditions.checkNotNull(defaultDate, Messages.MUST_NOT_BE_NULL, "default date");
        Date valueModelDate = (Date) valueModel.getValue();
        Date initialDate = valueModelDate != null ? valueModelDate : defaultDate;
        SpinnerDateModel spinnerModel = new SpinnerDateModel(initialDate, start, end, calendarField);
        connect(spinnerModel, valueModel, defaultDate);
        return spinnerModel;
    }

    public static SpinnerNumberModel createNumberAdapter(ValueModel valueModel, int defaultValue, int minValue, int maxValue, int stepSize) {
        return createNumberAdapter(valueModel, Integer.valueOf(defaultValue), Integer.valueOf(minValue), Integer.valueOf(maxValue), Integer.valueOf(stepSize));
    }

    public static SpinnerNumberModel createNumberAdapter(ValueModel valueModel, Number defaultValue, Comparable<? extends Number> minValue, Comparable<? extends Number> maxValue, Number stepSize) {
        Number valueModelNumber = (Number) valueModel.getValue();
        Number initialValue = valueModelNumber != null ? valueModelNumber : defaultValue;
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(initialValue, minValue, maxValue, stepSize);
        connect(spinnerModel, valueModel, defaultValue);
        return spinnerModel;
    }

    public static void connect(SpinnerModel spinnerModel, ValueModel valueModel, Object defaultValue) {
        SpinnerToValueModelConnector.connect(spinnerModel, valueModel, defaultValue);
    }
}

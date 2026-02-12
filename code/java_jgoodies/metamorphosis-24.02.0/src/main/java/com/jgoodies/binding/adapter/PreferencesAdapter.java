package com.jgoodies.binding.adapter;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.util.prefs.Preferences;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/adapter/PreferencesAdapter.class */
public final class PreferencesAdapter extends AbstractValueModel {
    private static final String ERROR_MSG = "Value must be a Boolean, Double, Float, Integer, Long, or String.";
    private final Preferences prefs;
    private final String key;
    private final Class<?> type;
    private final Object defaultValue;

    public PreferencesAdapter(Preferences prefs, String key, Object defaultValue) {
        this.prefs = (Preferences) Preconditions.checkNotNull(prefs, Messages.MUST_NOT_BE_NULL, "Preferences");
        this.key = (String) Preconditions.checkNotNull(key, Messages.MUST_NOT_BE_NULL, "key");
        this.defaultValue = Preconditions.checkNotNull(defaultValue, Messages.MUST_NOT_BE_NULL, "default value");
        Preconditions.checkArgument(isBackedType(defaultValue), "The Default Value must be a Boolean, Double, Float, Integer, Long, or String.");
        this.type = defaultValue.getClass();
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public Object getValue() {
        if (this.type == Boolean.class) {
            return Boolean.valueOf(getBoolean());
        }
        if (this.type == Double.class) {
            return Double.valueOf(getDouble());
        }
        if (this.type == Float.class) {
            return Float.valueOf(getFloat());
        }
        if (this.type == Integer.class) {
            return Integer.valueOf(getInt());
        }
        if (this.type == Long.class) {
            return Long.valueOf(getLong());
        }
        if (this.type == String.class) {
            return getString();
        }
        throw new ClassCastException(ERROR_MSG);
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public void setValue(Object newValue) {
        Preconditions.checkNotNull(newValue, Messages.MUST_NOT_BE_NULL, ValueModel.PROPERTY_VALUE);
        if (newValue instanceof Boolean) {
            setBoolean(((Boolean) newValue).booleanValue());
            return;
        }
        if (newValue instanceof Double) {
            setDouble(((Double) newValue).doubleValue());
            return;
        }
        if (newValue instanceof Float) {
            setFloat(((Float) newValue).floatValue());
            return;
        }
        if (newValue instanceof Integer) {
            setInt(((Integer) newValue).intValue());
        } else if (newValue instanceof Long) {
            setLong(((Long) newValue).longValue());
        } else if (newValue instanceof String) {
            setString((String) newValue);
        }
    }

    public boolean getBoolean() {
        return this.prefs.getBoolean(this.key, ((Boolean) this.defaultValue).booleanValue());
    }

    public double getDouble() {
        return this.prefs.getDouble(this.key, ((Double) this.defaultValue).doubleValue());
    }

    public float getFloat() {
        return this.prefs.getFloat(this.key, ((Float) this.defaultValue).floatValue());
    }

    public int getInt() {
        return this.prefs.getInt(this.key, ((Integer) this.defaultValue).intValue());
    }

    public long getLong() {
        return this.prefs.getLong(this.key, ((Long) this.defaultValue).longValue());
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public String getString() {
        return this.prefs.get(this.key, (String) this.defaultValue);
    }

    public void setBoolean(boolean newValue) {
        boolean oldValue = getBoolean();
        this.prefs.putBoolean(this.key, newValue);
        fireValueChange(oldValue, newValue);
    }

    public void setDouble(double newValue) {
        double oldValue = getDouble();
        this.prefs.putDouble(this.key, newValue);
        fireValueChange(oldValue, newValue);
    }

    public void setFloat(float newValue) {
        float oldValue = getFloat();
        this.prefs.putFloat(this.key, newValue);
        fireValueChange(oldValue, newValue);
    }

    public void setInt(int newValue) {
        int oldValue = getInt();
        this.prefs.putInt(this.key, newValue);
        fireValueChange(oldValue, newValue);
    }

    public void setLong(long newValue) {
        long oldValue = getLong();
        this.prefs.putLong(this.key, newValue);
        fireValueChange(oldValue, newValue);
    }

    public void setString(String newValue) {
        String oldValue = getString();
        this.prefs.put(this.key, newValue);
        fireValueChange(oldValue, newValue);
    }

    private static boolean isBackedType(Object value) {
        Class<?> aClass = value.getClass();
        return aClass == Boolean.class || aClass == Double.class || aClass == Float.class || aClass == Integer.class || aClass == Long.class || aClass == String.class;
    }
}

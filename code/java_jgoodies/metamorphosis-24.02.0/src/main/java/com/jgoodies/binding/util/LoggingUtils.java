package com.jgoodies.binding.util;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.bean.BeanUtils;
import com.jgoodies.common.internal.Messages;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/util/LoggingUtils.class */
public final class LoggingUtils {
    private static final Logger LOGGER = Logger.getLogger(LoggingUtils.class.getName());
    private static Level defaultLevel = Level.FINE;

    private LoggingUtils() {
    }

    public static void setDefaultLevel(Level level) {
        Preconditions.checkNotNull(level, Messages.MUST_NOT_BE_NULL, "log level");
        defaultLevel = level;
    }

    public static void logPropertyChanges(Object bean) {
        logPropertyChanges(bean, LOGGER);
    }

    public static void logPropertyChanges(Object bean, Logger logger) {
        logPropertyChanges(bean, logger, defaultLevel);
    }

    public static void logPropertyChanges(Object bean, Logger logger, Level level) {
        BeanUtils.addPropertyChangeListener(bean, new LogHandler(logger, level));
    }

    public static void logPropertyChanges(Object bean, String propertyName) {
        logPropertyChanges(bean, propertyName, LOGGER);
    }

    public static void logPropertyChanges(Object bean, String propertyName, Logger logger) {
        logPropertyChanges(bean, propertyName, logger, defaultLevel);
    }

    public static void logPropertyChanges(Object bean, String propertyName, Logger logger, Level level) {
        BeanUtils.addPropertyChangeListener(bean, propertyName, new LogHandler(logger, level));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/util/LoggingUtils$LogHandler.class */
    public static final class LogHandler implements PropertyChangeListener {
        private final Logger logger;
        private final Level level;

        LogHandler(Logger logger, Level level) {
            this.logger = (Logger) Preconditions.checkNotNull(logger, Messages.MUST_NOT_BE_NULL, "logger");
            this.level = (Level) Preconditions.checkNotNull(level, Messages.MUST_NOT_BE_NULL, "level");
        }

        @Override // java.beans.PropertyChangeListener
        public void propertyChange(PropertyChangeEvent e) {
            if (!this.logger.isLoggable(this.level)) {
                return;
            }
            Object newValue = e.getNewValue();
            Object oldValue = e.getOldValue();
            StringBuilder builder = new StringBuilder(e.getSource().toString());
            builder.append(" [propertyName=");
            builder.append(e.getPropertyName());
            builder.append(", oldValue=");
            builder.append(oldValue);
            if (oldValue != null) {
                builder.append(", oldValueType=");
                builder.append(oldValue.getClass());
            }
            builder.append(", newValue=");
            builder.append(newValue);
            if (newValue != null) {
                builder.append(", newValueType=");
                builder.append(newValue.getClass());
            }
            this.logger.log(this.level, builder.toString());
        }
    }
}

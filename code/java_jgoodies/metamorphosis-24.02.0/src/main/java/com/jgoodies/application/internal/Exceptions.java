package com.jgoodies.application.internal;

import com.jgoodies.application.ResourceMap;
import java.util.IllegalFormatException;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/internal/Exceptions.class */
public final class Exceptions {
    protected static final String FORMAT_STRING_RESOURCES = "%1$s\nBundle base name=\"%2$s\"\nKey=\"%3$s\"\nValue=\"%4$s\"";

    private Exceptions() {
    }

    public static String formatMessage(String cause, ResourceMap resourceMap, String key, String value) {
        String baseName = resourceMap != null ? resourceMap.getBaseName() : "no resource map";
        return String.format(FORMAT_STRING_RESOURCES, cause, baseName, key, value);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/internal/Exceptions$ListenerMethodInvalidSignatureException.class */
    public static final class ListenerMethodInvalidSignatureException extends RuntimeException {
        public ListenerMethodInvalidSignatureException(String message) {
            super(message);
        }

        public ListenerMethodInvalidSignatureException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/internal/Exceptions$ListenerMethodInvalidIdException.class */
    public static final class ListenerMethodInvalidIdException extends RuntimeException {
        public ListenerMethodInvalidIdException(String message) {
            super(message);
        }

        public ListenerMethodInvalidIdException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/internal/Exceptions$ListenerMethodInvalidNameException.class */
    public static final class ListenerMethodInvalidNameException extends RuntimeException {
        public ListenerMethodInvalidNameException(String message) {
            super(message);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/internal/Exceptions$ListenerMethodLookupException.class */
    public static final class ListenerMethodLookupException extends RuntimeException {
        public ListenerMethodLookupException(String message) {
            super(message);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/internal/Exceptions$ListenerMethodNotFoundException.class */
    public static final class ListenerMethodNotFoundException extends RuntimeException {
        public ListenerMethodNotFoundException(String message) {
            super(message);
        }

        public ListenerMethodNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/internal/Exceptions$ActionConfigurationException.class */
    public static final class ActionConfigurationException extends RuntimeException {
        public ActionConfigurationException(String message) {
            super(message);
        }

        public ActionConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/internal/Exceptions$ActionAnnotationValueException.class */
    public static final class ActionAnnotationValueException extends RuntimeException {
        public ActionAnnotationValueException(String message) {
            super(message);
        }

        public ActionAnnotationValueException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/internal/Exceptions$ActionNotFoundException.class */
    public static final class ActionNotFoundException extends RuntimeException {
        public ActionNotFoundException(String message) {
            super(message);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/internal/Exceptions$ResourceEvaluationException.class */
    public static final class ResourceEvaluationException extends RuntimeException {
        public ResourceEvaluationException(String message, ResourceMap resourceMap, String key, String value) {
            this(message, resourceMap, key, value, null);
        }

        public ResourceEvaluationException(String message, ResourceMap resourceMap, String key, String value, Throwable cause) {
            super(Exceptions.formatMessage(message, resourceMap, key, value), cause);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/internal/Exceptions$ResourceConversionException.class */
    public static final class ResourceConversionException extends RuntimeException {
        public ResourceConversionException(String message, ResourceMap resourceMap, String key, String value) {
            this(message, resourceMap, key, value, null);
        }

        public ResourceConversionException(String message, ResourceMap resourceMap, String key, String value, Throwable cause) {
            super(Exceptions.formatMessage(message, resourceMap, key, value), cause);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/internal/Exceptions$ResourceFormatException.class */
    public static final class ResourceFormatException extends RuntimeException {
        public ResourceFormatException(ResourceMap resourceMap, String key, String value, IllegalFormatException cause) {
            super(Exceptions.formatMessage("Illegal resource format string", resourceMap, key, value), cause);
        }
    }
}

package com.jgoodies.application.internal;

import com.jgoodies.application.Actions;
import com.jgoodies.application.ResourceConverter;
import com.jgoodies.application.ResourceConverters;
import com.jgoodies.application.ResourceMap;
import com.jgoodies.application.internal.Exceptions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.MnemonicUtils;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/internal/ActionAnnotationUtils.class */
public final class ActionAnnotationUtils {
    private static final String DEFAULT_FORMAT_STRING = "%1$s\nAction id  =%2$s\nMethod name=%3$s\nTarget class   =%4$s\nDeclaring class=%5$s";

    private ActionAnnotationUtils() {
    }

    public static void configureAction(Action action, String actionId, com.jgoodies.application.Action annotation, Object target, Method method, ResourceMap resourceMap, boolean isCustomResourceMap) {
        String messageDetails = "\nAction id  =" + actionId + "\nMethod name=" + method.getName() + "\nTarget class   =" + target.getClass().getName() + "\nDeclaring class=" + method.getDeclaringClass().getName() + "\nResourceMap base name=" + resourceMap.getBaseName() + (isCustomResourceMap ? " (custom ResourceMap)" : "");
        action.setEnabled(annotation.enabled());
        Actions.configure(action, resourceMap, actionId, messageDetails);
        String text = (String) action.getValue("Name");
        logInvalidEllipsisSuffix(text, actionId, method, target, resourceMap);
    }

    public static void configureAction(Action action, String actionId, com.jgoodies.application.Action annotation, Object target, Method method) {
        action.setEnabled(annotation.enabled());
        String markedText = annotation.text();
        if (Strings.isBlank(annotation.icon())) {
            checkAnnotationValue(Strings.isNotBlank(markedText), "The @Action annotation text attribute must not be empty or whitespace.", actionId, method, target, "text");
        } else {
            ResourceConverter converter = ResourceConverters.forType(Icon.class);
            action.putValue("SmallIcon", converter.convert(null, "no key", annotation.icon(), Icon.class));
        }
        ensureTrimmedAnnotationValue(method, target, actionId, "text", markedText);
        MnemonicUtils.configure(action, markedText);
        String text = (String) action.getValue("Name");
        logInvalidEllipsisSuffix(text, actionId, method, target);
        if (Strings.isNotBlank(annotation.accelerator())) {
            KeyStroke keyStroke = KeyStroke.getKeyStroke(annotation.accelerator());
            if (keyStroke == null) {
                String message = formatExceptionMessage("Invalid accelerator format.\nAccelerator=" + annotation.accelerator() + "\nValid examples: ctrl A, ctrl alt F, shift ctrl D", actionId, method, target);
                throw new Exceptions.ActionAnnotationValueException(message);
            }
            action.putValue("AcceleratorKey", keyStroke);
        }
        if (Strings.isNotBlank(annotation.shortDescription())) {
            logUntrimmedAnnotationValue(method, target, actionId, "short description", annotation.shortDescription());
            action.putValue("ShortDescription", annotation.shortDescription());
        }
        if (Strings.isNotBlank(annotation.longDescription())) {
            logUntrimmedAnnotationValue(method, target, actionId, "long description", annotation.longDescription());
            action.putValue("LongDescription", annotation.longDescription());
        }
        if (Strings.isNotBlank(annotation.accessibleName())) {
            ensureTrimmedAnnotationValue(method, target, actionId, "accessible name", annotation.accessibleName());
            action.putValue(Actions.ACCESSIBLE_NAME_KEY, annotation.accessibleName());
        }
        if (Strings.isNotBlank(annotation.accessibleDescription())) {
            logUntrimmedAnnotationValue(method, target, actionId, "accessible description", annotation.longDescription());
            action.putValue(Actions.ACCESSIBLE_DESCRIPTION_KEY, annotation.accessibleDescription());
        }
        if (Strings.isNotBlank(annotation.command())) {
            action.putValue("ActionCommandKey", annotation.command());
        }
    }

    public static boolean providesResources(com.jgoodies.application.Action annotation) {
        return Strings.isNotBlank(annotation.text()) || Strings.isNotBlank(annotation.icon()) || Strings.isNotBlank(annotation.accelerator()) || Strings.isNotBlank(annotation.shortDescription()) || Strings.isNotBlank(annotation.longDescription()) || Strings.isNotBlank(annotation.accessibleName()) || Strings.isNotBlank(annotation.accessibleDescription()) || Strings.isNotBlank(annotation.command());
    }

    private static void ensureTrimmedAnnotationValue(Method method, Object target, String actionId, String annotationName, String annotationValue) {
        String cause = String.format(Messages.MUST_BE_TRIMMED, "@Action annotation value");
        checkAnnotationValue(Strings.isTrimmed(annotationValue), cause, actionId, method, target, annotationName);
    }

    private static void logUntrimmedAnnotationValue(Method method, Object target, String actionId, String annotationName, String annotationValue) {
        if (Strings.isTrimmed(annotationValue)) {
            return;
        }
        String cause = String.format(Messages.MUST_BE_TRIMMED, "@Action annotation value");
        String message = formatExceptionMessage(cause, actionId, method, target) + "\nAnnotation name =" + annotationName + "\nAnnotation value=" + annotationValue;
        log(message);
    }

    private static void logInvalidEllipsisSuffix(String text, String actionId, Method method, Object target, ResourceMap map) {
        if (Strings.isBlank(text) || !text.endsWith(Strings.NO_ELLIPSIS_STRING)) {
            return;
        }
        String cause = String.format(Messages.USE_ELLIPSIS_NOT_THREE_DOTS, "Action text");
        String message = formatExceptionMessage(cause, actionId, method, target) + "\ntext key=" + actionId + ".Action.text\ntext    =" + text + "\nbundle  =" + map.getBaseName();
        log(message);
    }

    private static void logInvalidEllipsisSuffix(String text, String actionId, Method method, Object target) {
        if (Strings.isBlank(text) || !text.endsWith(Strings.NO_ELLIPSIS_STRING)) {
            return;
        }
        String cause = String.format(Messages.USE_ELLIPSIS_NOT_THREE_DOTS, "@Action annotation text");
        String message = formatExceptionMessage(cause, actionId, method, target) + "\ntext=" + text;
        log(message);
    }

    private static void log(String message) {
        Logger.getLogger(ActionAnnotationUtils.class.getName()).info(message);
    }

    private static void checkAnnotationValue(boolean expression, String cause, String actionId, Method method, Object target, String annotationName) {
        if (!expression) {
            throw new Exceptions.ActionAnnotationValueException(formatExceptionMessage(cause, actionId, method, target) + "\nAnnotation name=" + annotationName);
        }
    }

    private static String formatExceptionMessage(String cause, String actionId, Method method, Object target) {
        Object[] objArr = new Object[5];
        objArr[0] = cause;
        objArr[1] = actionId != null ? actionId : "-";
        objArr[2] = method.getName();
        objArr[3] = target.getClass().getName();
        objArr[4] = method.getDeclaringClass().getName();
        return String.format(DEFAULT_FORMAT_STRING, objArr);
    }
}

package com.jgoodies.application.internal;

import com.jgoodies.application.Action;
import com.jgoodies.application.internal.Exceptions;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import java.awt.event.ActionEvent;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/internal/ActionMethods.class */
public final class ActionMethods {
    private static ActionMethods instance;
    private static boolean allowsCustomNamingConventions = false;
    private final ActionMethodProcessor processor = new ActionMethodProcessor("on*Performed", ActionEvent.class);
    private SoftReference<Map<Class<?>, Map<String, MethodOrException>>> cachedMethods;

    public static boolean getAllowsCustomNamingConventions() {
        return allowsCustomNamingConventions;
    }

    public static void setAllowsCustomNamingConventions(boolean b) {
        allowsCustomNamingConventions = b;
        getInstance().clearCache();
    }

    private ActionMethods() {
    }

    public static ActionMethods getInstance() {
        if (instance == null) {
            instance = new ActionMethods();
        }
        return instance;
    }

    public synchronized Map<String, MethodOrException> getMethods(Class<?> type) {
        Map<Class<?>, Map<String, MethodOrException>> methods = getMethods();
        Map<String, MethodOrException> typeMethods = methods.get(type);
        if (typeMethods == null) {
            typeMethods = lookUpMethods(type);
            methods.put(type, typeMethods);
        }
        return typeMethods;
    }

    private Map<Class<?>, Map<String, MethodOrException>> getMethods() {
        if (this.cachedMethods != null && this.cachedMethods.get() != null) {
            return this.cachedMethods.get();
        }
        Map<Class<?>, Map<String, MethodOrException>> result = new HashMap<>();
        this.cachedMethods = new SoftReference<>(result);
        return result;
    }

    private Map<String, MethodOrException> lookUpMethods(Class<?> type) {
        MethodOrException newEntry;
        Map<String, MethodOrException> typeMethods = new HashMap<>();
        for (Method method : type.getMethods()) {
            Action annotation = (Action) method.getAnnotation(Action.class);
            if (annotation != null) {
                String id = this.processor.getId(method, annotation);
                try {
                    this.processor.ensureMethodNameFollowsNamingConvention(method);
                    ActionMethodProcessor.ensureValidParameterCount(method);
                    this.processor.ensureValidParameterType(method);
                    ActionMethodProcessor.ensureValidReturnType(method);
                    ActionMethodProcessor.ensureDeclaresNoExceptions(method);
                    MethodOrException oldEntry = typeMethods.get(id);
                    if (oldEntry == null || oldEntry.isException()) {
                        newEntry = new MethodOrException(method);
                    } else {
                        newEntry = new MethodOrException(new Exceptions.ListenerMethodLookupException(String.format("Duplicate methods annotated with @Action found for id.\nId=\"%1$s\"\nMethod 1=%2$s\nMethod 2=%3$s", id, oldEntry.getMethod(), method)));
                    }
                } catch (Exceptions.ListenerMethodLookupException e) {
                    newEntry = new MethodOrException(e);
                }
                typeMethods.put(id, newEntry);
            }
        }
        return typeMethods;
    }

    private void clearCache() {
        if (this.cachedMethods != null) {
            this.cachedMethods.clear();
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/internal/ActionMethods$MethodOrException.class */
    public static final class MethodOrException {
        private final Method method;
        private final RuntimeException exception;

        MethodOrException(Method method) {
            this.method = method;
            this.exception = null;
        }

        MethodOrException(RuntimeException e) {
            this.method = null;
            this.exception = e;
        }

        public boolean isMethod() {
            return this.method != null;
        }

        public boolean isException() {
            return this.exception != null;
        }

        public Method getMethod() {
            return this.method;
        }

        public void throwIfException() {
            if (isException()) {
                throw getException();
            }
        }

        public RuntimeException getException() {
            return this.exception;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/internal/ActionMethods$ActionMethodProcessor.class */
    public static final class ActionMethodProcessor {
        private static final String DEFAULT_FORMAT_STRING = "%1$s\nMethod=%2$s";
        private final String defaultIdPrefix;
        private final String defaultIdSuffix;
        private final Class<?> parameterType;

        ActionMethodProcessor(String defaultIdPattern, Class<?> parameterType) {
            this.parameterType = parameterType;
            Preconditions.checkNotBlank(defaultIdPattern, "The pattern for the default id must not be blank.");
            int starIndex = defaultIdPattern.indexOf(42);
            this.defaultIdPrefix = defaultIdPattern.substring(0, starIndex);
            this.defaultIdSuffix = defaultIdPattern.substring(starIndex + 1);
        }

        private Class<?> getParameterType() {
            return this.parameterType;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getId(Method method, Action annotation) {
            String idValue = annotation.id();
            if (Strings.isNotEmpty(idValue)) {
                checkIdTrimmed(idValue, "The method id must not start or end with whitespace.\nid='" + idValue + "'", method);
                return idValue;
            }
            return getDefaultId(method);
        }

        private String getDefaultId(Method method) {
            String methodName = method.getName();
            if (methodNameFollowsConvention(methodName)) {
                String name = methodName.substring(this.defaultIdPrefix.length(), methodName.length() - this.defaultIdSuffix.length());
                return name.length() == 0 ? methodName : name;
            }
            return methodName;
        }

        private boolean methodNameFollowsConvention(String methodName) {
            return methodName.startsWith(this.defaultIdPrefix) && methodName.endsWith(this.defaultIdSuffix);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void ensureMethodNameFollowsNamingConvention(Method method) {
            if (!ActionMethods.getAllowsCustomNamingConventions() && !methodNameFollowsConvention(method.getName())) {
                String cause = String.format("Methods annotated with @Action must start with '%1$s' and end with '%2$s'.", this.defaultIdPrefix, this.defaultIdSuffix);
                throw new Exceptions.ListenerMethodInvalidNameException(formatExceptionMessage(cause, method));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void ensureValidParameterCount(Method method) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            checkSignature(parameterTypes.length == 1, method, "Methods annotated with @Action must have 1 parameter.", new Object[0]);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void ensureValidParameterType(Method method) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            checkSignature(parameterTypes.length == 0 || parameterTypes[0] == getParameterType(), method, "Methods annotated with @Action must have a parameter type\n %1$s.", getParameterType().getSimpleName());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void ensureValidReturnType(Method method) {
            Class<?> returnType = method.getReturnType();
            checkSignature(returnType == Void.TYPE, method, "Methods annotated with @Action must return void.", new Object[0]);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void ensureDeclaresNoExceptions(Method method) {
            Class<?>[] exceptionTypes = method.getExceptionTypes();
            checkSignature(exceptionTypes == null || exceptionTypes.length == 0, method, "Methods annotated with @Action must not declare exceptions to be thrown.", new Object[0]);
        }

        private static void checkSignature(boolean expression, Method method, String causeFormat, Object... causeArgs) {
            if (!expression) {
                String cause = Strings.get(causeFormat, causeArgs);
                throw new Exceptions.ListenerMethodInvalidSignatureException(formatExceptionMessage(cause, method));
            }
        }

        private static void checkIdTrimmed(String id, String cause, Method method) {
            if (!Strings.isTrimmed(id)) {
                throw new Exceptions.ListenerMethodInvalidIdException(formatExceptionMessage(cause, method));
            }
        }

        private static String formatExceptionMessage(String cause, Method method) {
            return String.format(DEFAULT_FORMAT_STRING, cause, method.toString());
        }
    }
}
